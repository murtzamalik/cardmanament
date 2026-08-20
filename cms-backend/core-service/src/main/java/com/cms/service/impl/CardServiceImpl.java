package com.cms.service.impl;

import com.cms.dal.entity.*;
import com.cms.dal.repository.*;
import com.cms.dto.request.*;
import com.cms.dto.response.*;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.BranchMapper;
import com.cms.mapper.CardMapper;
import com.cms.service.CardDataEncryptionService;
import com.cms.service.CardService;
import com.cms.service.NewCardRequestService;
import com.cms.service.AccountEligibilityService;
import com.cms.spec.CardSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cms.dal.entity.CardRequest;
import java.util.List;
import com.cms.dto.request.ExportReadyRequest;
import com.cms.dto.request.BulkExportRequest;
import com.cms.dto.request.BulkRenewRequest;
import com.cms.service.CardTrackDataFormatter;
import com.cms.service.CvvGenerationService;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;




import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardAccountRepository cardAccountRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardProductRepository cardProductRepository;
    private final BranchRepository branchRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;
    private final BranchMapper branchMapper;
    private final LimitProfileRepository limitProfileRepository;
    private final CardDataEncryptionService encryptionService;
    private final NewCardRequestService newCardRequestService;
    private final CardRequestRepository cardRequestRepository;
    private final com.cms.service.CardExportFileService cardExportFileService;
    private final CardTrackDataFormatter cardTrackDataFormatter;
    private final CvvGenerationService cvvGenerationService;
    private final AccountEligibilityService accountEligibilityService;


    public CardServiceImpl(CardRepository cardRepository, CardAccountRepository cardAccountRepository,
                           CardStatusRepository cardStatusRepository, CardTypeRepository cardTypeRepository,
                           CardProductRepository cardProductRepository, BranchRepository branchRepository,
                           AccountRepository accountRepository, CardMapper cardMapper, BranchMapper branchMapper,
                           LimitProfileRepository limitProfileRepository, CardDataEncryptionService encryptionService,
                           NewCardRequestService newCardRequestService,
                           CardRequestRepository cardRequestRepository,
                           com.cms.service.CardExportFileService cardExportFileService, CardTrackDataFormatter cardTrackDataFormatter, CvvGenerationService cvvGenerationService,
                           AccountEligibilityService accountEligibilityService) {
        this.cardRepository = cardRepository;
        this.cardAccountRepository = cardAccountRepository;
        this.cardStatusRepository = cardStatusRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.cardProductRepository = cardProductRepository;
        this.branchRepository = branchRepository;
        this.accountRepository = accountRepository;
        this.cardMapper = cardMapper;
        this.branchMapper = branchMapper;
        this.limitProfileRepository = limitProfileRepository;
        this.encryptionService = encryptionService;
        this.newCardRequestService = newCardRequestService;
        this.cardRequestRepository = cardRequestRepository;
        this.cardExportFileService = cardExportFileService;
        this.cardTrackDataFormatter = cardTrackDataFormatter;
        this.cvvGenerationService = cvvGenerationService;
        this.accountEligibilityService = accountEligibilityService;
    }

    /**
     * Resolve PAN for internal use (e.g. link/delink). Prefer decrypted from panEncrypted.
     */
    private String resolvePan(Card card) {
        if (card.getPanEncrypted() != null && !card.getPanEncrypted().isBlank()) {
            try {
                return encryptionService.decrypt(card.getPanEncrypted());
            } catch (RuntimeException ignore) {
                // Fall through to legacy plain PAN if encrypted value is not readable.
            }
        }
        String pan = card.getPan();
        if (pan != null && pan.contains("*")) return null;
        return pan;
    }

    private boolean isHotStatusCode(String statusCode) {
        if (statusCode == null) return false;
        String code = statusCode.trim();
        return "HOT".equalsIgnoreCase(code) || "003".equals(code);
    }

    @Override
    public List<CardResponse> getAllCards() {
        return cardMapper.toResponseList(cardRepository.findAll());
    }

    @Override
    public PageResponse<CardResponse> searchCards(CardSearchRequest request) {
        Sort sort = "asc".equalsIgnoreCase(request.getSortDir())
                ? Sort.by(request.getSort()).ascending()
                : Sort.by(request.getSort()).descending();
        var pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Specification<Card> spec = CardSpecification.fromSearch(request).and(CardSpecification.fetchTypeStatusProductBranch());
        var page = cardRepository.findAll(spec, pageable);
        List<CardResponse> content = cardMapper.toResponseList(page.getContent());
        fillNamesFromCodes(content);
        PageResponse<CardResponse> pr = new PageResponse<>();
        pr.setContent(content);
        pr.setPage(page.getNumber());
        pr.setSize(page.getSize());
        pr.setTotalElements(page.getTotalElements());
        pr.setTotalPages(page.getTotalPages());
        return pr;
    }

    /**
     * When association IDs are null, names can still be resolved from codes (e.g. cardTypeCode -> cardTypeName).
     */
    private void fillNamesFromCodes(List<CardResponse> list) {
        if (list == null || list.isEmpty()) return;
        Set<String> typeCodes = list.stream().filter(r -> r.getCardTypeName() == null && r.getCardTypeCode() != null).map(CardResponse::getCardTypeCode).collect(Collectors.toSet());
        Set<String> statusCodes = list.stream().filter(r -> r.getCardStatusName() == null && r.getCardStatusCode() != null).map(CardResponse::getCardStatusCode).collect(Collectors.toSet());
        Set<String> productCodes = list.stream().filter(r -> r.getProductName() == null && r.getProductCode() != null).map(CardResponse::getProductCode).collect(Collectors.toSet());
        Set<String> branchCodes = list.stream().filter(r -> r.getBranchName() == null && r.getBranchCode() != null).map(CardResponse::getBranchCode).collect(Collectors.toSet());
        Map<String, String> typeNames = new HashMap<>();
        Map<String, String> statusNames = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();
        Map<String, String> branchNames = new HashMap<>();
        for (String code : typeCodes) {
            cardTypeRepository.findByCardTypeCode(code).ifPresent(t -> typeNames.put(code, t.getCardTypeName()));
        }
        for (String code : statusCodes) {
            cardStatusRepository.findByCardStatusCode(code).ifPresent(s -> statusNames.put(code, s.getCardStatusName()));
        }
        for (String code : productCodes) {
            cardProductRepository.findByProductCode(code).ifPresent(p -> productNames.put(code, p.getProductName()));
        }
        for (String code : branchCodes) {
            branchRepository.findByBranchCode(code).ifPresent(b -> branchNames.put(code, b.getBranchName()));
        }
        for (CardResponse r : list) {
            if (r.getCardTypeName() == null && r.getCardTypeCode() != null)
                r.setCardTypeName(typeNames.get(r.getCardTypeCode()));
            if (r.getCardStatusName() == null && r.getCardStatusCode() != null)
                r.setCardStatusName(statusNames.get(r.getCardStatusCode()));
            if (r.getProductName() == null && r.getProductCode() != null)
                r.setProductName(productNames.get(r.getProductCode()));
            if (r.getBranchName() == null && r.getBranchCode() != null)
                r.setBranchName(branchNames.get(r.getBranchCode()));
        }
    }

    @Override
    public CardResponse getCardByPan(String pan) {
        String hash = encryptionService.panHashForLookup(pan);
        Optional<Card> byHash = hash != null ? cardRepository.findByPanHash(hash) : Optional.<Card>empty();
        Card card = byHash.or(() -> cardRepository.findByPan(pan))
                .orElseThrow(() -> new ResourceNotFoundException("Card", "PAN"));
        CardResponse response = cardMapper.toResponse(card);
        if (response != null) fillNamesFromCodes(List.of(response));
        return response;
    }

    @Override
    public CardResponse getCardById(Long cardId) {
        Card card = cardRepository.findByIdWithDetails(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        CardResponse response = cardMapper.toResponse(card);
        fillNamesFromCodes(response != null ? List.of(response) : List.of());
        return response;
    }

    @Override
    public CardDropdownsResponse getDropdowns() {
        CardDropdownsResponse r = new CardDropdownsResponse();
        r.setBranches(branchMapper.toResponseList(branchRepository.findAll()));
        List<CardDropdownsResponse.CardStatusItem> statuses = new ArrayList<>();
        cardStatusRepository.findAll().forEach(s -> {
            var i = new CardDropdownsResponse.CardStatusItem();
            i.setId(s.getId());
            i.setCode(s.getCardStatusCode());
            i.setName(s.getCardStatusName());
            statuses.add(i);
        });
        r.setCardStatuses(statuses);
        List<CardDropdownsResponse.CardProductItem> products = new ArrayList<>();
        cardProductRepository.findAll().forEach(p -> {
            var i = new CardDropdownsResponse.CardProductItem();
            i.setId(p.getId());
            i.setCode(p.getProductCode());
            i.setName(p.getProductName());
            products.add(i);
        });
        r.setCardProducts(products);
        List<CardDropdownsResponse.CardTypeItem> types = new ArrayList<>();
        cardTypeRepository.findAllByOrderByCardTypeCode().forEach(t -> {
            var i = new CardDropdownsResponse.CardTypeItem();
            i.setId(t.getId());
            i.setCode(t.getCardTypeCode());
            i.setName(t.getCardTypeName());
            i.setProductCode(t.getProductCode());
            types.add(i);
        });
        r.setCardTypes(types);
        List<CardDropdownsResponse.LimitProfileItem> limitProfiles = new ArrayList<>();
        limitProfileRepository.findByIsActiveOrderByProfileCodeAsc(1).forEach(lp -> {
            var i = new CardDropdownsResponse.LimitProfileItem();
            i.setId(lp.getId());
            i.setCode(lp.getProfileCode());
            i.setName(lp.getProfileName() != null ? lp.getProfileName() : lp.getProfileCode());
            limitProfiles.add(i);
        });
        r.setLimitProfiles(limitProfiles);
        return r;
    }

    @Override
    public List<AccountOptionResponse> getAvailableAccounts(String relationshipNum) {
        List<Account> accounts = accountRepository.findAll();
        List<AccountOptionResponse> out = new ArrayList<>();
        for (Account a : accounts) {
            if (!accountEligibilityService.isEligibleForCardOrLink(a)) {
                continue;
            }
            AccountOptionResponse o = new AccountOptionResponse();
            o.setAccountNum(a.getAccountNum());
            o.setAccountTitle(a.getAccountTitle());
            out.add(o);
        }
        return out;
    }

    @Override
    @Transactional
    public void linkCardAccount(LinkCardAccountRequest request) {
        String hash = encryptionService.panHashForLookup(request.getPan());
        Card card = (hash != null ? cardRepository.findByPanHash(hash) : Optional.<Card>empty())
                .or(() -> cardRepository.findByPan(request.getPan()))
                .orElseThrow(() -> new ResourceNotFoundException("Card", "PAN"));
        Account account = accountRepository.findByAccountNum(request.getAccountNum())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getAccountNum()));
        accountEligibilityService.requireEligibleForCardOrLink(account);
        boolean alreadyLinked = cardAccountRepository.findByCardId(card.getCardId()).stream()
                .anyMatch(ca -> request.getAccountNum().equals(ca.getAccountNum()));
        if (alreadyLinked) {
            throw new BusinessValidationException("Account already linked to this card");
        }
        String panMasked = encryptionService.maskPan(request.getPan());
        CardAccount ca = new CardAccount();
        ca.setCardId(card.getCardId());
        ca.setPan(panMasked);
        ca.setAccountNum(request.getAccountNum());
        ca.setAccountId(account.getAccountId());
        ca.setRelationshipNum(request.getRelationshipNum());
        ca.setEffectiveFrom(LocalDateTime.now());
        ca.setEffectiveTo(LocalDateTime.now().plusYears(50));
        ca.setIsOverallDefault(Boolean.TRUE.equals(request.getIsOverallDefault()) ? 1 : 0);
        ca.setIsAcctTypeDefault(Boolean.TRUE.equals(request.getIsAcctTypeDefault()) ? 1 : 0);
        ca.setCreatedOn(LocalDateTime.now());
        ca.setUpdatedOn(LocalDateTime.now());
        ca.setCreatedBy("system");
        ca.setUpdatedBy("system");
        cardAccountRepository.save(ca);
    }

    @Override
    @Transactional
    public void linkCardAccountByCardId(Long cardId, LinkCardAccountByCardIdRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        String pan = resolvePan(card);
        if (pan == null || pan.isBlank())
            throw new BusinessValidationException("Card has no PAN");
        LinkCardAccountRequest req = new LinkCardAccountRequest();
        req.setPan(pan);
        req.setAccountNum(request.getAccountNum());
        req.setRelationshipNum(request.getRelationshipNum() != null ? request.getRelationshipNum() : card.getRelationshipNum());
        req.setIsOverallDefault(request.getIsOverallDefault());
        req.setIsAcctTypeDefault(request.getIsAcctTypeDefault());
        linkCardAccount(req);
    }

    @Override
    @Transactional
    public void delinkCardAccount(String pan, String accountNum) {
        String hash = encryptionService.panHashForLookup(pan);
        Card card = (hash != null ? cardRepository.findByPanHash(hash) : Optional.<Card>empty())
                .or(() -> cardRepository.findByPan(pan))
                .orElseThrow(() -> new ResourceNotFoundException("Card", "PAN"));
        CardAccount ca = cardAccountRepository.findByCardId(card.getCardId()).stream()
                .filter(link -> accountNum.equals(link.getAccountNum()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CardAccount", "PAN/" + accountNum));
        cardAccountRepository.deleteById(ca.getCaId());
    }

    @Override
    @Transactional
    public void delinkCardAccountByCardId(Long cardId, String accountNum) {
        CardAccount ca = cardAccountRepository.findByCardId(cardId).stream()
                .filter(link -> accountNum.equals(link.getAccountNum()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CardAccount", cardId + "/" + accountNum));
        cardAccountRepository.deleteById(ca.getCaId());
    }

    @Override
    @Transactional
    public CardResponse updateCard(Long cardId, CardUpdateRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        boolean cardIsHot = isHotStatusCode(card.getCardStatusCode());
        if (request.getCardStatusId() != null) {
            CardStatus cs = cardStatusRepository.findById(request.getCardStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("CardStatus", String.valueOf(request.getCardStatusId())));
            if (cardIsHot && !isHotStatusCode(cs.getCardStatusCode())) {
                throw new BusinessValidationException("HOT card status cannot be changed.");
            }
            card.setCardStatusCode(cs.getCardStatusCode());
        } else if (request.getCardStatusCode() != null) {
            if (cardIsHot && !isHotStatusCode(request.getCardStatusCode())) {
                throw new BusinessValidationException("HOT card status cannot be changed.");
            }
            card.setCardStatusCode(request.getCardStatusCode());
        }
        if (request.getLimitProfileId() != null) {
            LimitProfile lp = limitProfileRepository.findById(request.getLimitProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(request.getLimitProfileId())));
            // LIMIT_PROFILE is NUMBER in DB — store id, not profile code (avoids ORA-01722 for codes like STD)
            card.setLimitProfile(String.valueOf(lp.getId()));
            card.setLimitProfileId(lp.getId());
        } else if (request.getLimitProfile() != null) {
            if (request.getLimitProfile().isBlank()) {
                card.setLimitProfile(null);
                card.setLimitProfileId(null);
            } else {
                LimitProfile lp = resolveLimitProfileForCard(request.getLimitProfile())
                        .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", request.getLimitProfile()));
                card.setLimitProfile(String.valueOf(lp.getId()));
                card.setLimitProfileId(lp.getId());
            }
        }
        if (request.getCardTitle() != null) card.setCardTitle(request.getCardTitle());
        card.setUpdatedOn(LocalDateTime.now());
        return cardMapper.toResponse(cardRepository.save(card));
    }

    @Override
    @Transactional
    public void linkLimitProfile(Long cardId, Long limitProfileId, String limitProfile) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        if (limitProfileId != null) {
            LimitProfile lp = limitProfileRepository.findById(limitProfileId)
                    .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(limitProfileId)));
            card.setLimitProfile(String.valueOf(lp.getId()));
            card.setLimitProfileId(lp.getId());
        } else if (limitProfile != null && !limitProfile.isBlank()) {
            LimitProfile lp = resolveLimitProfileForCard(limitProfile)
                    .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", limitProfile));
            card.setLimitProfile(String.valueOf(lp.getId()));
            card.setLimitProfileId(lp.getId());
        } else {
            card.setLimitProfile(null);
            card.setLimitProfileId(null);
        }
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);
    }

    /** Resolve by numeric id or profile code (e.g. STD). */
    private java.util.Optional<LimitProfile> resolveLimitProfileForCard(String configured) {
        if (configured == null || configured.isBlank()) {
            return java.util.Optional.empty();
        }
        String value = configured.trim();
        if (value.matches("\\d+")) {
            return limitProfileRepository.findById(Long.parseLong(value));
        }
        return limitProfileRepository.findByProfileCode(value);
    }

    @Override
    public List<CardAccountLinkResponse> getLinkedAccountsByCardId(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Card", String.valueOf(cardId));
        }
        return toLinkResponses(cardAccountRepository.findByCardId(cardId));
    }

    @Override
    public List<CardAccountLinkResponse> getLinkedAccountsByPan(String pan) {
        String hash = encryptionService.panHashForLookup(pan);
        Optional<Card> card = (hash != null ? cardRepository.findByPanHash(hash) : Optional.<Card>empty())
                .or(() -> cardRepository.findByPan(pan));
        if (card.isEmpty()) return List.of();
        return toLinkResponses(cardAccountRepository.findByCardId(card.get().getCardId()));
    }

    private List<CardAccountLinkResponse> toLinkResponses(List<CardAccount> list) {
        List<CardAccountLinkResponse> out = new ArrayList<>();
        for (CardAccount ca : list) {
            CardAccountLinkResponse r = new CardAccountLinkResponse();
            r.setPanMasked(cardMapper.maskPan(ca.getPan()));
            r.setAccountNum(ca.getAccountNum());
            r.setEffectiveFrom(ca.getEffectiveFrom());
            r.setEffectiveTo(ca.getEffectiveTo());
            r.setIsOverallDefault(ca.getIsOverallDefault());
            r.setIsAcctTypeDefault(ca.getIsAcctTypeDefault());
            if (ca.getAccount() != null) r.setAccountTitle(ca.getAccount().getAccountTitle());
            out.add(r);
        }
        return out;
    }

    @Override
    @Transactional
    public void closeCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        card.setCardStatusCode("003");
        card.setWhenDeleted(LocalDateTime.now());
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);
    }

    @Override
    public CustomerInfoResponse getCustomerByRelation(String rel, String linked, Long cardId) {
        CustomerInfoResponse r = new CustomerInfoResponse();
        r.setRelationshipNum(rel);
        return r;
    }

    @Override
    public List<CardResponse> getExportReadyCards(ExportReadyRequest request) {
        if (request.getCardTypeId() == null && (request.getCardTypeCode() == null || request.getCardTypeCode().isBlank()))
            throw new BusinessValidationException("cardTypeId or cardTypeCode is required");
        // Resolve card type code from ID if provided
        String cardTypeCode = request.getCardTypeCode();
        if (request.getCardTypeId() != null) {
            cardTypeCode = cardTypeRepository.findById(request.getCardTypeId())
                    .map(t -> t.getCardTypeCode())
                    .orElse(cardTypeCode);
        }
        List<Card> cards = cardRepository.findByCardProdStatusIdAndCardTypeCode("001", cardTypeCode);
        return cardMapper.toResponseList(cards);
    }

    @Override
    @Transactional
    public String bulkExport(BulkExportRequest request) {
        if (request.getCardIds() == null || request.getCardIds().isEmpty())
            throw new BusinessValidationException("cardIds is required");
        List<Card> cards = cardRepository.findAllById(request.getCardIds());
        if (cards.isEmpty())
            throw new ResourceNotFoundException("Cards", "provided IDs");
        String exportPath = cardExportFileService.generateBulkExportFile(cards);
        for (Card card : cards) {
            card.setCardProdStatusId("002");
            card.setExportFilePath(exportPath);
            card.setUpdatedOn(LocalDateTime.now());
        }
        cardRepository.saveAll(cards);
        return exportPath;
    }



    @Override
    public List<CardResponse> searchByExpiryDate(ExpirySearchRequest request) {
        LocalDateTime from = request.getDateFrom() != null
                ? request.getDateFrom().atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        LocalDateTime to = request.getDateTo() != null
                ? request.getDateTo().atTime(23, 59, 59)
                : LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        List<Card> cards = cardRepository.findByExpiryDateBetween(from, to);
        String panQuery = request.getPan() != null ? request.getPan().replaceAll("\\D", "") : "";
        if (!panQuery.isBlank()) {
            String last4Query = panQuery.length() >= 4 ? panQuery.substring(panQuery.length() - 4) : panQuery;
            String hash = encryptionService.panHashForLookup(panQuery);
            cards = cards.stream()
                    .filter(c -> {
                        if (hash != null && hash.equals(c.getPanHash())) return true;
                        String last4 = c.getPanLast4() != null ? c.getPanLast4().replaceAll("\\D", "") : "";
                        String pan = c.getPan() != null ? c.getPan().replaceAll("\\D", "") : "";
                        return (!last4.isBlank() && last4.contains(last4Query))
                                || (!pan.isBlank() && pan.contains(panQuery));
                    })
                    .toList();
        }
        return cardMapper.toResponseList(cards);
    }

    @Override
    @Transactional
    public Long changeCardType(Long cardId, ChangeCardTypeRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        assertEligibleForOnceOnlyFlow(card, "CHANGE_TYPE");

        CardType cardType = cardTypeRepository.findById(request.getCardTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(request.getCardTypeId())));
        if (cardType.getIsActive() == null || cardType.getIsActive() != 1) {
            throw new BusinessValidationException("Card Type is not active");
        }

        if (card.getRelationshipNum() == null || card.getRelationshipNum().isBlank()) {
            throw new BusinessValidationException("Card has no relationship number; cannot create change type request.");
        }
        List<CardAccount> linkedAccounts = cardAccountRepository.findByCardId(cardId);
        String accountNum = linkedAccounts.isEmpty() ? null : linkedAccounts.get(0).getAccountNum();
        if (accountNum == null || accountNum.isBlank()) {
            throw new BusinessValidationException("Link an account to this card before changing type.");
        }
        if (card.getProductCode() == null || card.getProductCode().isBlank()) {
            throw new BusinessValidationException("Card has no product; cannot create change type request.");
        }
        if (card.getBranchCode() == null || card.getBranchCode().isBlank()) {
            throw new BusinessValidationException("Card has no branch; cannot create change type request.");
        }

        NewCardRequestCreate newRequest = new NewCardRequestCreate();
        newRequest.setRelationshipNum(card.getRelationshipNum());
        newRequest.setAccountNum(accountNum);
        newRequest.setCardTitle(card.getCardTitle());
        newRequest.setCardTypeId(cardType.getId());
        newRequest.setCardTypeCode(cardType.getCardTypeCode());
        newRequest.setProductCode(card.getProductCode());
        newRequest.setBranchCode(card.getBranchCode());
        newRequest.setRequestTypeId("CHANGE_TYPE");
        newRequest.setSourceCardId(cardId);

        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);
        CardRequestResponse response = newCardRequestService.create(newRequest, "system");
        return response.getRequestId();
    }

    @Override
    @Transactional
    public Long replacementRequest(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        assertEligibleForOnceOnlyFlow(card, "REPLACEMENT");

        if (card.getRelationshipNum() == null || card.getRelationshipNum().isBlank()) {
            throw new BusinessValidationException("Card has no relationship number; cannot create replacement request.");
        }
        List<CardAccount> linkedAccounts = cardAccountRepository.findByCardId(cardId);
        String accountNum = linkedAccounts.isEmpty() ? null : linkedAccounts.get(0).getAccountNum();
        if (accountNum == null || accountNum.isBlank()) {
            throw new BusinessValidationException("Link an account to this card before requesting replacement.");
        }
        if (card.getCardTypeCode() == null || card.getCardTypeCode().isBlank()) {
            throw new BusinessValidationException("Card has no card type; cannot create replacement request.");
        }
        if (card.getProductCode() == null || card.getProductCode().isBlank()) {
            throw new BusinessValidationException("Card has no product; cannot create replacement request.");
        }
        if (card.getBranchCode() == null || card.getBranchCode().isBlank()) {
            throw new BusinessValidationException("Card has no branch; cannot create replacement request.");
        }

        // Do not mark old card Hot yet — that happens on approve/generate after the new card is created.
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);

        NewCardRequestCreate newRequest = new NewCardRequestCreate();
        newRequest.setRelationshipNum(card.getRelationshipNum());
        newRequest.setAccountNum(accountNum);
        newRequest.setCardTitle(card.getCardTitle());
        newRequest.setCardTypeCode(card.getCardTypeCode());
        newRequest.setProductCode(card.getProductCode());
        newRequest.setBranchCode(card.getBranchCode());
        newRequest.setRequestTypeId("REPLACEMENT");
        newRequest.setSourceCardId(cardId);

        CardRequestResponse response = newCardRequestService.create(newRequest, "system");
        return response.getRequestId();
    }

    /**
     * Change-type and replacement are once-only per source card.
     * Reject Hot / already-replaced cards and any open request of the same type.
     */
    private void assertEligibleForOnceOnlyFlow(Card card, String requestTypeId) {
        if (isHotStatusCode(card.getCardStatusCode())) {
            throw new BusinessValidationException("HOT cards cannot be changed or replaced.");
        }
        if (card.getIsReplaced() != null && card.getIsReplaced() == 1) {
            throw new BusinessValidationException("This card has already been replaced.");
        }
        String existingType = card.getRequestType();
        if ("CHANGE_TYPE".equalsIgnoreCase(requestTypeId)
                && existingType != null && "CHANGE_TYPE".equalsIgnoreCase(existingType.trim())) {
            throw new BusinessValidationException("Card type can only be changed once for this card.");
        }
        if ("REPLACEMENT".equalsIgnoreCase(requestTypeId)
                && existingType != null && "REPLACEMENT".equalsIgnoreCase(existingType.trim())) {
            throw new BusinessValidationException("Replacement can only be requested once for this card.");
        }
        if (cardRequestRepository.existsBySourceCardIdAndRequestTypeIdAndIsProcessed(
                card.getCardId(), requestTypeId, 0)) {
            throw new BusinessValidationException(
                    "An open " + requestTypeId + " request already exists for this card.");
        }
    }

    @Override
    @Transactional
    public List<Long> bulkRenew(BulkRenewRequest request) {
        if (request.getCardIds() == null || request.getCardIds().isEmpty())
            throw new BusinessValidationException("cardIds is required");

        List<Long> renewedIds = new ArrayList<>();
        List<Card> cards = cardRepository.findAllById(request.getCardIds());
        if (cards.isEmpty()) {
            throw new ResourceNotFoundException("Cards", "provided IDs");
        }
        if (cards.size() != request.getCardIds().size()) {
            throw new BusinessValidationException("One or more selected card IDs are invalid");
        }

        for (Card card : cards) {
            YearMonth baseExpiryMonth = card.getExpiryDate() != null
                    ? YearMonth.from(card.getExpiryDate())
                    : YearMonth.now();
            YearMonth newExpiryMonth = baseExpiryMonth.plusYears(5);
            LocalDateTime newExpiry = newExpiryMonth.atEndOfMonth().atTime(23, 59, 59);
            card.setExpiryDate(newExpiry);

            String expiryYyMm = newExpiry.format(DateTimeFormatter.ofPattern("yyMM"));

            String pan = null;
            if (card.getPanEncrypted() != null && !card.getPanEncrypted().isBlank()) {
                try {
                    pan = encryptionService.decrypt(card.getPanEncrypted());
                } catch (RuntimeException ex) {
                    // Backward compatibility for legacy rows where encrypted value may be invalid.
                    String legacy = card.getPan();
                    pan = (legacy != null && !legacy.contains("*")) ? legacy : null;
                }
            } else {
                String legacy = card.getPan();
                pan = (legacy != null && !legacy.contains("*")) ? legacy : null;
            }
            if (pan == null || pan.isBlank()) {
                throw new BusinessValidationException("PAN missing for cardId " + card.getCardId());
            }

            CvvGenerationService.CvvResult cvvResult = cvvGenerationService.generate(pan, expiryYyMm);

            String track1 = cardTrackDataFormatter.formatTrack1(pan, expiryYyMm, card.getCardTitle(), cvvResult.cvv1());
            String track2 = cardTrackDataFormatter.formatTrack2(pan, expiryYyMm, cvvResult.cvv1());

            card.setCvv(encryptionService.encrypt(cvvResult.cvv1()));
            card.setCvv2(encryptionService.encrypt(cvvResult.cvv2()));
            card.setIcvv(encryptionService.encrypt(cvvResult.icvv()));
            card.setTrack1Data(encryptionService.encrypt(track1));
            card.setTrack2Data(encryptionService.encrypt(track2));

            card.setCardProdStatusId("001");
            card.setUpdatedOn(LocalDateTime.now());

            cardRepository.save(card);
            renewedIds.add(card.getCardId());
        }
        return renewedIds;
    }

}
