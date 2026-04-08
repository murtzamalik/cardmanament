package com.cms.service.impl;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.CardAccount;
import com.cms.dal.entity.CardRequest;
import com.cms.dal.repository.AccountRepository;
import com.cms.dal.repository.CardAccountRepository;
import com.cms.dal.repository.CardRepository;
import com.cms.dal.repository.CardRequestRepository;
import com.cms.dal.repository.CardTypeRepository;
import com.cms.dto.response.CardGenerationResultResponse;
import com.cms.dto.response.CardRequestResponse;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardMapper;
import com.cms.mapper.CardRequestMapper;
import com.cms.service.CardDataEncryptionService;
import com.cms.service.CardExportFileService;
import com.cms.service.CardGenerationService;
import com.cms.service.CardTrackDataFormatter;
import com.cms.service.CvvGenerationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.time.YearMonth;
import java.util.Random; // Modified this code for PAN generation


@Service
public class CardGenerationServiceImpl implements CardGenerationService {

    private final CardRequestRepository cardRequestRepository;
    private final CardRepository cardRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardRequestMapper cardRequestMapper;
    private final CardMapper cardMapper;
    private final CardDataEncryptionService encryptionService;
    private final CardExportFileService cardExportFileService;
    private final CvvGenerationService cvvGenerationService;
    private final CardTrackDataFormatter cardTrackDataFormatter;
    private final AccountRepository accountRepository;
    private final CardAccountRepository cardAccountRepository;

    private static final DateTimeFormatter EXPIRY_YYMM = DateTimeFormatter.ofPattern("yyMM");

    public CardGenerationServiceImpl(CardRequestRepository cardRequestRepository, CardRepository cardRepository,
                                    CardTypeRepository cardTypeRepository, CardRequestMapper cardRequestMapper,
                                    CardMapper cardMapper, CardDataEncryptionService encryptionService,
                                    CardExportFileService cardExportFileService,
                                    CvvGenerationService cvvGenerationService,
                                    CardTrackDataFormatter cardTrackDataFormatter,
                                    AccountRepository accountRepository,
                                    CardAccountRepository cardAccountRepository) {
        this.cardRequestRepository = cardRequestRepository;
        this.cardRepository = cardRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.cardRequestMapper = cardRequestMapper;
        this.cardMapper = cardMapper;
        this.encryptionService = encryptionService;
        this.cardExportFileService = cardExportFileService;
        this.cvvGenerationService = cvvGenerationService;
        this.cardTrackDataFormatter = cardTrackDataFormatter;
        this.accountRepository = accountRepository;
        this.cardAccountRepository = cardAccountRepository;
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
        Card card = new Card();
        card.setRelationshipNum(req.getRelationshipNum());
        // Modified code for setting title to 19 chars
        String rawTitle = req.getCardTitle() != null ? req.getCardTitle().trim() : "";
        card.setCardTitle(String.format("%-19s", rawTitle));
        card.setCardTypeCode(req.getCardTypeCode());
        card.setProductCode(req.getProductCode());
        card.setBranchCode(req.getBranchCode());

        // Modified this code for PAN generation
        String generatedPan = generatePan(req.getCardTypeId(), req.getCardTypeCode());
        // Modified this code for PAN generation
        card.setPan(generatedPan);
        card.setPanEncrypted(encryptionService.encrypt(generatedPan));
        card.setPanLast4(encryptionService.panLast4(generatedPan));
        card.setPanHash(encryptionService.panHashForLookup(generatedPan));
        card.setTrimPan(generatedPan);
        YearMonth expiryMonth = YearMonth.now().plusYears(5);
        card.setExpiryDate(expiryMonth.atEndOfMonth().atTime(23, 59, 59));
        //card.setExpiryDate(LocalDateTime.now().plusYears(5));
        card.setCardStatusCode("ACTIVE");
        card.setCreatedOn(LocalDateTime.now());
        card.setUpdatedOn(LocalDateTime.now());
        card.setCreatedBy("system");
        card.setUpdatedBy("system");
        card.setIsReplaced(0);
        card.setIssuedDate(LocalDateTime.now());
        card.setActivationDate(null);


        String expiryYyMm = card.getExpiryDate().format(EXPIRY_YYMM);
        CvvGenerationService.CvvResult cvvResult = cvvGenerationService.generate(generatedPan, expiryYyMm);
        String track1 = cardTrackDataFormatter.formatTrack1(generatedPan, expiryYyMm, card.getCardTitle(), cvvResult.cvv1());
        String track2 = cardTrackDataFormatter.formatTrack2(generatedPan, expiryYyMm, cvvResult.cvv1());

        card.setCvv(base64Encode(cvvResult.cvv1()));
        card.setCvv2(base64Encode(cvvResult.cvv2()));
        card.setIcvv(base64Encode(cvvResult.icvv()));
        card.setTrack1Data(base64Encode(track1));
        card.setTrack2Data(base64Encode(track2));

        Card saved = cardRepository.save(card);
        req.setIsProcessed(1);
        req.setProgressFlag(1);
        req.setPrimaryPan(generatedPan);
        req.setUpdatedOn(LocalDateTime.now());
        cardRequestRepository.save(req);
        //Format Setting here.

        String accountNum = req.getAccountNum();
        if (accountNum != null && !accountNum.isBlank()) {
            if (cardAccountRepository.findByPanAndAccountNum(generatedPan, accountNum).isEmpty()) {
                var account = accountRepository.findByAccountNum(accountNum)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", accountNum));
                CardAccount ca = new CardAccount();
                ca.setCardId(saved.getCardId());
                ca.setPan(generatedPan);
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
        result.setPanMasked(saved.getPanLast4() != null ? "****" + saved.getPanLast4() : cardMapper.maskPan(saved.getPan()));
        return result;
    }

    private static String base64Encode(String value) {
        if (value == null) return null;
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    // Modified this code for PAN generation
    private String generatePan(Long cardTypeId, String cardTypeCode) {
        // Get BIN from CardType — fallback to 900419 if not found
        int bin = 900419;
        if (cardTypeId != null) {
            var cardType = cardTypeRepository.findById(cardTypeId).orElse(null);
            if (cardType != null && cardType.getBin() != null) {
                bin = cardType.getBin();
            }
        } else if (cardTypeCode != null) {
            var cardType = cardTypeRepository.findByCardTypeCode(cardTypeCode).orElse(null);
            if (cardType != null && cardType.getBin() != null) {
                bin = cardType.getBin();
            }
        }
        // BIN padded to 6 digits
        String binStr = String.format("%06d", bin);
        // Generate 9 random numeric digits
        Random random = new Random();
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            middle.append(random.nextInt(10));
        }
        // 15 digits = BIN(6) + middle(9)
        String pan15 = binStr + middle;
        // Calculate Luhn check digit
        int luhn = calculateLuhnDigit(pan15);
        return pan15 + luhn;
    }

    // Modified this code for PAN generation — Luhn algorithm check digit
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
    // Modified this code for PAN generation

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
        CardGenerationResultResponse result = processNewCardGeneration(requestId);
        if (!result.isSuccess()) {
            return result;
        }
        if (result.getCardId() != null) {
            Card card = cardRepository.findById(result.getCardId()).orElse(null);
            if (card != null) {
                String exportPath = cardExportFileService.generateExportFile(card, requestId);
                result.setExportFilePath(exportPath);
                if (exportPath != null && !exportPath.isBlank()) {
                    card.setExportFilePath(exportPath);
                    cardRepository.save(card);
                }
            }
        }
        return result;
    }
}
