package com.cms.service.impl;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.CardAccount;
import com.cms.dal.entity.CardRequest;
import com.cms.dal.entity.CardType;
import com.cms.dal.entity.LimitProfile;
import com.cms.dal.repository.AccountRepository;
import com.cms.dal.repository.CardAccountRepository;
import com.cms.dal.repository.CardRepository;
import com.cms.dal.repository.CardRequestRepository;
import com.cms.dal.repository.CardTypeRepository;
import com.cms.dal.repository.LimitProfileRepository;
import com.cms.dto.response.CardGenerationResultResponse;
import com.cms.dto.response.CardRequestResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardMapper;
import com.cms.mapper.CardRequestMapper;
import com.cms.service.CardDataEncryptionService;
import com.cms.service.CardGenerationService;
import com.cms.service.CardTrackDataFormatter;
import com.cms.service.CvvGenerationService;
import com.cms.service.AccountEligibilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.Optional;


@Service
public class CardGenerationServiceImpl implements CardGenerationService {

    private static final Logger log = LoggerFactory.getLogger(CardGenerationServiceImpl.class);

    private final CardRequestRepository cardRequestRepository;
    private final CardRepository cardRepository;
    private final CardTypeRepository cardTypeRepository;
    private final LimitProfileRepository limitProfileRepository;
    private final CardRequestMapper cardRequestMapper;
    private final CardMapper cardMapper;
    private final CardDataEncryptionService encryptionService;
    private final CvvGenerationService cvvGenerationService;
    private final CardTrackDataFormatter cardTrackDataFormatter;
    private final AccountRepository accountRepository;
    private final CardAccountRepository cardAccountRepository;
    private final AccountEligibilityService accountEligibilityService;
    private final String mobileDefaultLimitProfile;

    private static final DateTimeFormatter EXPIRY_YYMM = DateTimeFormatter.ofPattern("yyMM");

    public CardGenerationServiceImpl(CardRequestRepository cardRequestRepository, CardRepository cardRepository,
                                    CardTypeRepository cardTypeRepository,
                                    LimitProfileRepository limitProfileRepository,
                                    CardRequestMapper cardRequestMapper,
                                    CardMapper cardMapper, CardDataEncryptionService encryptionService,
                                    CvvGenerationService cvvGenerationService,
                                    CardTrackDataFormatter cardTrackDataFormatter,
                                    AccountRepository accountRepository,
                                    CardAccountRepository cardAccountRepository,
                                    AccountEligibilityService accountEligibilityService,
                                    @Value("${cms.card.mobile-default-limit-profile:STD}") String mobileDefaultLimitProfile) {
        this.cardRequestRepository = cardRequestRepository;
        this.cardRepository = cardRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.limitProfileRepository = limitProfileRepository;
        this.cardRequestMapper = cardRequestMapper;
        this.cardMapper = cardMapper;
        this.encryptionService = encryptionService;
        this.cvvGenerationService = cvvGenerationService;
        this.cardTrackDataFormatter = cardTrackDataFormatter;
        this.accountRepository = accountRepository;
        this.cardAccountRepository = cardAccountRepository;
        this.accountEligibilityService = accountEligibilityService;
        this.mobileDefaultLimitProfile = mobileDefaultLimitProfile;
    }

    @Override
    public List<CardRequestResponse> getCardRequestByCode(String relationshipNum, String accountNum) {
        List<CardRequest> list = cardRequestRepository.findByRelationshipNumAndAccountNum(relationshipNum, accountNum);
        return cardRequestMapper.toResponseList(list);
    }

    @Override
    @Transactional
    public CardGenerationResultResponse processNewCardGeneration(Long requestId) {
        CardGenerationResultResponse result = new CardGenerationResultResponse();
        CardRequest req = cardRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        if (req.getIsProcessed() != null && req.getIsProcessed() == 1) {
            result.setSuccess(false);
            result.setMessage("Request already processed");
            return result;
        }
        String accountNumForCheck = req.getAccountNum();
        if (accountNumForCheck != null && !accountNumForCheck.isBlank()) {
            accountEligibilityService.requireEligibleForCardOrLink(accountNumForCheck);
        }

        String requestType = req.getRequestTypeId() != null ? req.getRequestTypeId().trim() : "";
        boolean isChangeTypeOrReplacement = "CHANGE_TYPE".equalsIgnoreCase(requestType)
                || "REPLACEMENT".equalsIgnoreCase(requestType);

        // Always create a new card row for CHANGE_TYPE / REPLACEMENT (old card stays and becomes Hot).
        Card card = new Card();
        card.setRelationshipNum(req.getRelationshipNum());
        // Modified code for setting title to 19 chars
        String rawTitle = req.getCardTitle() != null ? req.getCardTitle().trim() : "";
        card.setCardTitle(String.format("%-19s", rawTitle));
        card.setCardTypeCode(req.getCardTypeCode());
        card.setProductCode(req.getProductCode());
        card.setBranchCode(req.getBranchCode());
        if (!requestType.isBlank()) {
            card.setRequestType(requestType);
        }

        // Modified this code for PAN generation — use cardTypeCode since cardTypeId removed from CardRequest
        String generatedPan = generatePan(null, req.getCardTypeCode());
        String panMasked = encryptionService.maskPan(generatedPan);
        // Store masked PAN only in clear columns; full PAN lives in PAN_ENCRYPTED
        card.setPan(panMasked);
        card.setPanEncrypted(encryptionService.encrypt(generatedPan));
        card.setPanLast4(encryptionService.panLast4(generatedPan));
        card.setPanHash(encryptionService.panHashForLookup(generatedPan));
        card.setTrimPan(panMasked);
        YearMonth expiryMonth = YearMonth.now().plusYears(5);
        card.setExpiryDate(expiryMonth.atEndOfMonth().atTime(23, 59, 59));
        // CHANGE_TYPE / REPLACEMENT: Warm (002) until activated by call etc. Normal NEW: Cold (001).
        card.setCardStatusCode(isChangeTypeOrReplacement ? "002" : "001");
        card.setCreatedOn(LocalDateTime.now());
        card.setUpdatedOn(LocalDateTime.now());
        card.setCreatedBy("system");
        card.setUpdatedBy("system");
        card.setIsReplaced(0);
        card.setIssuedDate(LocalDateTime.now());
        card.setActivationDate(null);
        card.setCardProdStatusId("001"); // 001 = Issued, card generated not yet exported

        // Assign limit profile: card-type default first, then configured STD fallback
        applyLimitProfileOnGenerate(req, card);

        String expiryYyMm = card.getExpiryDate().format(EXPIRY_YYMM);
        CvvGenerationService.CvvResult cvvResult = cvvGenerationService.generate(generatedPan, expiryYyMm);
        String track1 = cardTrackDataFormatter.formatTrack1(generatedPan, expiryYyMm, card.getCardTitle(), cvvResult.cvv1());
        String track2 = cardTrackDataFormatter.formatTrack2(generatedPan, expiryYyMm, cvvResult.cvv1());

        card.setCvv(encryptionService.encrypt(cvvResult.cvv1()));
        card.setCvv2(encryptionService.encrypt(cvvResult.cvv2()));
        card.setIcvv(encryptionService.encrypt(cvvResult.icvv()));
        card.setTrack1Data(encryptionService.encrypt(track1));
        card.setTrack2Data(encryptionService.encrypt(track2));

        Card saved = cardRepository.save(card);

        if (isChangeTypeOrReplacement) {
            markSourceCardHot(req.getSourceCardId());
        }

        req.setIsProcessed(1);
        req.setProgressFlag(1);
        req.setPrimaryPan(panMasked);
        req.setUpdatedOn(LocalDateTime.now());
        cardRequestRepository.save(req);
        //Format Setting here.

        String accountNum = req.getAccountNum();
        if (accountNum != null && !accountNum.isBlank()) {
            boolean alreadyLinked = cardAccountRepository.findByCardId(saved.getCardId()).stream()
                .anyMatch(ca -> accountNum.equals(ca.getAccountNum()));
            if (!alreadyLinked) {
                var account = accountRepository.findByAccountNum(accountNum)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", accountNum));
                CardAccount ca = new CardAccount();
                ca.setCardId(saved.getCardId());
                ca.setPan(panMasked);
                ca.setAccountNum(accountNum);
                ca.setAccountId(account.getAccountId());
                ca.setRelationshipNum(req.getRelationshipNum() != null ? req.getRelationshipNum() : "");
                ca.setEffectiveFrom(LocalDateTime.now());
                ca.setEffectiveTo(LocalDateTime.now().plusYears(50));
                ca.setIsOverallDefault(1);
                ca.setIsAcctTypeDefault(0);
                ca.setCreatedOn(LocalDateTime.now());
                ca.setUpdatedOn(LocalDateTime.now());
                ca.setCreatedBy("system");
                ca.setUpdatedBy("system");
                cardAccountRepository.save(ca);
            }
        }

        result.setSuccess(true);
        result.setMessage("Card generated successfully");
        result.setCardId(saved.getCardId());
        result.setPanMasked(panMasked);
        return result;
    }

    /**
     * After CHANGE_TYPE / REPLACEMENT generation: old source card becomes Hot (003).
     */
    private void markSourceCardHot(Long sourceCardId) {
        if (sourceCardId == null) {
            log.warn("CHANGE_TYPE/REPLACEMENT request has no sourceCardId; cannot mark old card Hot");
            return;
        }
        Card oldCard = cardRepository.findById(sourceCardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(sourceCardId)));
        oldCard.setCardStatusCode("003");
        oldCard.setIsReplaced(1);
        oldCard.setUpdatedOn(LocalDateTime.now());
        oldCard.setUpdatedBy("system");
        cardRepository.save(oldCard);
        log.info("Marked source card {} as Hot (003) after new card generation", sourceCardId);
    }

    /**
     * On approve/generate: assign limit profile to the new card (portal or mobile).
     * <ol>
     *   <li>Card type {@code DEFAULT_LIMIT_PROFILE_ID} if configured</li>
     *   <li>Else configured fallback {@code cms.card.mobile-default-limit-profile} (default STD)</li>
     * </ol>
     * Skips if the card already has a limit profile.
     */
    private void applyLimitProfileOnGenerate(CardRequest req, Card card) {
        if (card.getLimitProfile() != null && !card.getLimitProfile().isBlank()) {
            return;
        }
        if (card.getLimitProfileId() != null) {
            return;
        }

        Optional<LimitProfile> fromCardType = resolveLimitProfileFromCardType(req.getCardTypeCode());
        if (fromCardType.isPresent()) {
            assignLimitProfile(card, fromCardType.get(), "card type " + req.getCardTypeCode());
            return;
        }

        String configured = mobileDefaultLimitProfile != null ? mobileDefaultLimitProfile.trim() : "STD";
        if (configured.isEmpty()) {
            log.warn("Card request {}: no card-type limit and no fallback profile configured; card generated without limit",
                req.getRequestId());
            return;
        }
        Optional<LimitProfile> fallback = resolveLimitProfile(configured);
        if (fallback.isEmpty()) {
            log.warn("Card request {}: limit profile '{}' not found; card generated without limit",
                req.getRequestId(), configured);
            return;
        }
        assignLimitProfile(card, fallback.get(), "fallback " + configured);
    }

    private Optional<LimitProfile> resolveLimitProfileFromCardType(String cardTypeCode) {
        if (cardTypeCode == null || cardTypeCode.isBlank()) {
            return Optional.empty();
        }
        return cardTypeRepository.findByCardTypeCode(cardTypeCode.trim())
            .map(CardType::getDefaultLimitProfileId)
            .filter(id -> id != null)
            .flatMap(limitProfileRepository::findById);
    }

    private void assignLimitProfile(Card card, LimitProfile lp, String source) {
        card.setLimitProfile(String.valueOf(lp.getId()));
        card.setLimitProfileId(lp.getId());
        log.info("Assigned limit profile id {} (code {}) to card from {}",
            lp.getId(), lp.getProfileCode(), source);
    }

    private Optional<LimitProfile> resolveLimitProfile(String configured) {
        if (configured.matches("\\d+")) {
            return limitProfileRepository.findById(Long.parseLong(configured));
        }
        return limitProfileRepository.findByProfileCode(configured);
    }

    private String generatePan(Long cardTypeId, String cardTypeCode) {
        CardType cardType;
        if (cardTypeId != null) {
            cardType = cardTypeRepository.findById(cardTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(cardTypeId)));
        } else if (cardTypeCode != null && !cardTypeCode.isBlank()) {
            cardType = cardTypeRepository.findByCardTypeCode(cardTypeCode)
                .orElseThrow(() -> new ResourceNotFoundException("CardType", cardTypeCode));
        } else {
            throw new BusinessValidationException("Card type is required for PAN generation");
        }

        if (cardType.getProduct() == null) {
            throw new BusinessValidationException(
                "Card type " + cardType.getCardTypeCode() + " is not linked to a product");
        }
        Integer bin = cardType.getProduct().getBin();
        if (bin == null) {
            throw new BusinessValidationException(
                "BIN is not configured for product " + cardType.getProduct().getProductCode());
        }
        if (bin < 100000 || bin > 999999) {
            throw new BusinessValidationException(
                "BIN for product " + cardType.getProduct().getProductCode() + " must be exactly 6 digits");
        }

        String binStr = String.valueOf(bin);
        Random random = new Random();
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            middle.append(random.nextInt(10));
        }
        String pan15 = binStr + middle;
        int luhn = calculateLuhnDigit(pan15);
        return pan15 + luhn;
    }

    private int calculateLuhnDigit(String pan15) {
        int sum = 0;
        boolean alternate = true;
        for (int i = pan15.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(String.valueOf(pan15.charAt(i)));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
    @Override
    @Transactional
    public void updateCardRequestProgress(Long requestId, Integer progressFlag) {
        CardRequest req = cardRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        req.setProgressFlag(progressFlag);
        req.setUpdatedOn(LocalDateTime.now());
        cardRequestRepository.save(req);
    }
    @Override
    @Transactional
    public CardGenerationResultResponse approveAndGenerate(Long requestId) {
        CardRequest req = cardRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        if (req.getIsProcessed() != null && req.getIsProcessed() == 1) {
            CardGenerationResultResponse already = new CardGenerationResultResponse();
            already.setSuccess(false);
            already.setMessage("Request already processed");
            return already;
        }
        updateCardRequestProgress(requestId, 1);
        return processNewCardGeneration(requestId);
    }
}
