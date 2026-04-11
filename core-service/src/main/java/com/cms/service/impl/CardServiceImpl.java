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
import com.cms.spec.CardSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cms.dal.entity.CardRequest;
import java.util.List;


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


    public CardServiceImpl(CardRepository cardRepository, CardAccountRepository cardAccountRepository,
                           CardStatusRepository cardStatusRepository, CardTypeRepository cardTypeRepository,
                           CardProductRepository cardProductRepository, BranchRepository branchRepository,
                           AccountRepository accountRepository, CardMapper cardMapper, BranchMapper branchMapper,
                           LimitProfileRepository limitProfileRepository, CardDataEncryptionService encryptionService, NewCardRequestService newCardRequestService) {
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
    }

    /**
     * Resolve PAN for internal use (e.g. link/delink). Prefer decrypted from panEncrypted.
     */
    private String resolvePan(Card card) {
        if (card.getPanEncrypted() != null && !card.getPanEncrypted().isBlank()) {
            return encryptionService.decrypt(card.getPanEncrypted());
        }
        return card.getPan();
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
        Card card = cardRepository.findByPan(request.getPan())
                .orElseThrow(() -> new ResourceNotFoundException("Card", "PAN " + request.getPan()));
        Account account = accountRepository.findByAccountNum(request.getAccountNum())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getAccountNum()));
        if (cardAccountRepository.findByPanAndAccountNum(request.getPan(), request.getAccountNum()).stream().findFirst().isPresent())
            throw new BusinessValidationException("Account already linked to this card");
        CardAccount ca = new CardAccount();
        ca.setCardId(card.getCardId());
        ca.setPan(request.getPan());
        ca.setAccountNum(request.getAccountNum());
        ca.setAccountId(account.getAccountId());
        ca.setRelationshipNum(request.getRelationshipNum());
        ca.setEffectiveFrom(LocalDateTime.now());
        ca.setEffectiveTo(LocalDateTime.now().plusYears(50));
        ca.setIsOverallDefault(Boolean.TRUE.equals(request.getIsOverallDefault()) ? 1 : 0);
        ca.setIsAcctTypeDefault(Boolean.TRUE.equals(request.getIsAcctTypeDefault()) ? 1 : 0);
        ca.setCreatedOn(LocalDateTime.now());
        ca.setUpdatedOn(LocalDateTime.now());
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
        CardAccount ca = cardAccountRepository.findByPanAndAccountNum(pan, accountNum).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CardAccount", pan + "/" + accountNum));
        cardAccountRepository.deleteById(ca.getCaId());
    }

    @Override
    @Transactional
    public void delinkCardAccountByCardId(Long cardId, String accountNum) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        String pan = resolvePan(card);
        if (pan == null || pan.isBlank())
            throw new BusinessValidationException("Card has no PAN");
        delinkCardAccount(pan, accountNum);
    }

    @Override
    @Transactional
    public CardResponse updateCard(Long cardId, CardUpdateRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        if (request.getCardStatusId() != null) {
            CardStatus cs = cardStatusRepository.findById(request.getCardStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("CardStatus", String.valueOf(request.getCardStatusId())));
            card.setCardStatusId(cs.getId());
            card.setCardStatusCode(cs.getCardStatusCode());
        } else if (request.getCardStatusCode() != null) {
            card.setCardStatusCode(request.getCardStatusCode());
            cardStatusRepository.findByCardStatusCode(request.getCardStatusCode())
                    .ifPresent(cs -> card.setCardStatusId(cs.getId()));
        }
        if (request.getLimitProfileId() != null) {
            LimitProfile lp = limitProfileRepository.findById(request.getLimitProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(request.getLimitProfileId())));
            card.setLimitProfile(lp.getProfileCode());
            card.setLimitProfileId(lp.getId());
        } else if (request.getLimitProfile() != null) {
            if (request.getLimitProfile().isBlank()) {
                card.setLimitProfile(null);
                card.setLimitProfileId(null);
            } else {
                LimitProfile lp = limitProfileRepository.findByProfileCode(request.getLimitProfile())
                        .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", request.getLimitProfile()));
                card.setLimitProfile(lp.getProfileCode());
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
            card.setLimitProfile(lp.getProfileCode());
            card.setLimitProfileId(lp.getId());
        } else if (limitProfile != null && !limitProfile.isBlank()) {
            LimitProfile lp = limitProfileRepository.findByProfileCode(limitProfile)
                    .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", limitProfile));
            card.setLimitProfile(lp.getProfileCode());
            card.setLimitProfileId(lp.getId());
        } else {
            card.setLimitProfile(null);
            card.setLimitProfileId(null);
        }
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);
    }

    @Override
    public List<CardAccountLinkResponse> getLinkedAccountsByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        String pan = resolvePan(card);
        if (pan == null || pan.isBlank()) return List.of();
        return getLinkedAccountsByPan(pan);
    }

    @Override
    public List<CardAccountLinkResponse> getLinkedAccountsByPan(String pan) {
        List<CardAccount> list = cardAccountRepository.findByPan(pan);
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
        card.setCardStatusCode("CLOSED");
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
    public List<CardResponse> searchByExpiryDate(ExpirySearchRequest request) {
        LocalDateTime from = request.getDateFrom() != null
                ? request.getDateFrom().atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        LocalDateTime to = request.getDateTo() != null
                ? request.getDateTo().atTime(23, 59, 59)
                : LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        List<Card> cards = cardRepository.findByExpiryDateBetween(from, to);
        return cardMapper.toResponseList(cards);
    }

    @Override
    @Transactional
    public void changeCardType(Long cardId, ChangeCardTypeRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));
        CardType cardType = cardTypeRepository.findById(request.getCardTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(request.getCardTypeId())));
        if (cardType.getIsActive() == null || cardType.getIsActive() != 1) {
            throw new BusinessValidationException("Card Type is not active");
        }
        card.setCardTypeId(cardType.getId());
        card.setCardTypeCode(cardType.getCardTypeCode());
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public Long replacementRequest(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", String.valueOf(cardId)));

        if (card.getRelationshipNum() == null || card.getRelationshipNum().isBlank()) {
            throw new BusinessValidationException("Card has no relationship number; cannot create replacement request.");
        }
        List<CardAccount> linkedAccounts = cardAccountRepository.findByCardId(cardId);
        String accountNum = linkedAccounts.isEmpty() ? null : linkedAccounts.get(0).getAccountNum();
        if (accountNum == null || accountNum.isBlank()) {
            throw new BusinessValidationException("Link an account to this card before requesting replacement.");
        }
        if (card.getCardTypeId() == null && (card.getCardTypeCode() == null || card.getCardTypeCode().isBlank())) {
            throw new BusinessValidationException("Card has no card type; cannot create replacement request.");
        }
        if (card.getCardProductId() == null && (card.getProductCode() == null || card.getProductCode().isBlank())) {
            throw new BusinessValidationException("Card has no product; cannot create replacement request.");
        }
        if (card.getBranchId() == null && (card.getBranchCode() == null || card.getBranchCode().isBlank())) {
            throw new BusinessValidationException("Card has no branch; cannot create replacement request.");
        }

        card.setCardStatusCode("INACTIVE");
        card.setIsReplaced(1);
        card.setUpdatedOn(LocalDateTime.now());
        cardRepository.save(card);

        NewCardRequestCreate newRequest = new NewCardRequestCreate();
        newRequest.setRelationshipNum(card.getRelationshipNum());
        newRequest.setAccountNum(accountNum);
        newRequest.setCardTitle(card.getCardTitle());
        newRequest.setCardTypeId(card.getCardTypeId());
        newRequest.setCardTypeCode(card.getCardTypeCode());
        newRequest.setProductId(card.getCardProductId());
        newRequest.setProductCode(card.getProductCode());
        newRequest.setBranchId(card.getBranchId());
        newRequest.setBranchCode(card.getBranchCode());
        newRequest.setRequestTypeId("REPLACEMENT");

        CardRequestResponse response = newCardRequestService.create(newRequest, "system");
        return response.getRequestId();
    }


}
