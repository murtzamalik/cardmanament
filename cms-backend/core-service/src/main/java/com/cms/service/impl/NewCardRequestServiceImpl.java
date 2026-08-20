package com.cms.service.impl;

import com.cms.dal.entity.Account;
import com.cms.dal.entity.CardRequest;
import com.cms.dal.repository.AccountRepository;
import com.cms.dal.repository.AccountStatusRepository;
import com.cms.dal.repository.AccountTypeRepository;
import com.cms.dal.repository.BranchRepository;
import com.cms.dal.repository.CardRequestRepository;
import com.cms.dal.repository.CardTypeRepository;
import com.cms.dal.repository.CardProductRepository;
import com.cms.dto.request.NewAccountRequest;
import com.cms.dto.request.NewCardRequestCreate;
import com.cms.dto.response.CardRequestResponse;
import com.cms.dto.response.CustomerInfoResponse;
import com.cms.dto.response.PageResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardRequestMapper;
import com.cms.service.AccountEligibilityService;
import com.cms.service.NewCardRequestService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NewCardRequestServiceImpl implements NewCardRequestService {

    private static final String DEFAULT_ACCOUNT_STATUS = "OPEN";

    private final CardRequestRepository cardRequestRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardProductRepository cardProductRepository;
    private final CardRequestMapper cardRequestMapper;
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final BranchRepository branchRepository;
    private final AccountEligibilityService accountEligibilityService;

    public NewCardRequestServiceImpl(CardRequestRepository cardRequestRepository,
                                      CardTypeRepository cardTypeRepository,
                                      CardProductRepository cardProductRepository,
                                      CardRequestMapper cardRequestMapper,
                                      AccountRepository accountRepository,
                                      AccountTypeRepository accountTypeRepository,
                                      AccountStatusRepository accountStatusRepository,
                                      BranchRepository branchRepository,
                                      AccountEligibilityService accountEligibilityService) {
        this.cardRequestRepository = cardRequestRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.cardProductRepository = cardProductRepository;
        this.cardRequestMapper = cardRequestMapper;
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.accountStatusRepository = accountStatusRepository;
        this.branchRepository = branchRepository;
        this.accountEligibilityService = accountEligibilityService;
    }

    @Override
    @Transactional
    public CardRequestResponse create(NewCardRequestCreate request, String createdBy) {
        if (request.getCardTypeId() == null && (request.getCardTypeCode() == null || request.getCardTypeCode().isBlank()))
            throw new BusinessValidationException("Either cardTypeId or cardTypeCode is required");
        if (request.getProductId() == null && (request.getProductCode() == null || request.getProductCode().isBlank()))
            throw new BusinessValidationException("Either productId or productCode is required");
        if (request.getBranchId() == null && (request.getBranchCode() == null || request.getBranchCode().isBlank()))
            throw new BusinessValidationException("Either branchId or branchCode is required");
        String cardTitle = normalizeCardTitle(request.getCardTitle());
        String accountNum = resolveAccountNum(request, createdBy);
        Long branchId = resolveBranchId(request);
        Long cardTypeId = resolveCardTypeId(request);
        Long productId = resolveProductId(request);
        CardRequest cr = new CardRequest();
        cr.setRelationshipNum(request.getRelationshipNum());
        cr.setAccountNum(accountNum);
        cr.setCardTitle(cardTitle);
        branchRepository.findById(branchId).ifPresent(b -> cr.setBranchCode(b.getBranchCode()));        cardTypeRepository.findById(cardTypeId).ifPresent(t -> cr.setCardTypeCode(t.getCardTypeCode()));
        cardProductRepository.findById(productId).ifPresent(p -> cr.setProductCode(p.getProductCode()));
        cr.setSupplementaryCount(request.getSupplementaryCount() != null ? request.getSupplementaryCount() : 0);
        cr.setRequestTypeId(resolveRequestTypeId(request));
        cr.setSourceCardId(request.getSourceCardId());
        cr.setIsProcessed(0);
        cr.setProgressFlag(0);
        cr.setCreatedOn(LocalDateTime.now());
        cr.setCreatedBy(createdBy);
        cr.setUpdatedOn(LocalDateTime.now());
        cr.setUpdatedBy(createdBy);
        return enrich(cardRequestMapper.toResponse(cardRequestRepository.save(cr)));
    }

    private Long resolveBranchId(NewCardRequestCreate request) {
        if (request.getBranchId() != null)
            return branchRepository.findById(request.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", String.valueOf(request.getBranchId()))).getId();
        return branchRepository.findByBranchCode(request.getBranchCode()).orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchCode() != null ? request.getBranchCode() : "null")).getId();
    }

    private Long resolveCardTypeId(NewCardRequestCreate request) {
        if (request.getCardTypeId() != null)
            return cardTypeRepository.findById(request.getCardTypeId()).orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(request.getCardTypeId()))).getId();
        return cardTypeRepository.findByCardTypeCode(request.getCardTypeCode()).orElseThrow(() -> new ResourceNotFoundException("CardType", request.getCardTypeCode() != null ? request.getCardTypeCode() : "null")).getId();
    }

    private Long resolveProductId(NewCardRequestCreate request) {
        if (request.getProductId() != null)
            return cardProductRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("CardProduct", String.valueOf(request.getProductId()))).getId();
        return cardProductRepository.findByProductCode(request.getProductCode()).orElseThrow(() -> new ResourceNotFoundException("CardProduct", request.getProductCode() != null ? request.getProductCode() : "null")).getId();
    }

    /**
     * If newAccount is present, create the account and return its accountNum.
     * Otherwise require existing accountNum and validate it exists.
     */
    private String resolveAccountNum(NewCardRequestCreate request, String createdBy) {
        if (request.getNewAccount() != null) {
            NewAccountRequest na = request.getNewAccount();
            String num = na.getAccountNum() != null ? na.getAccountNum().trim() : null;
            if (num == null || num.isEmpty())
                throw new BusinessValidationException("accountNum is required when creating a new account");
            if (accountRepository.findByAccountNum(num).isPresent())
                throw new BusinessValidationException("Account already exists: " + num);
            if (na.getAccountTypeId() == null && (na.getAcctTypeCode() == null || na.getAcctTypeCode().isBlank()))
                throw new BusinessValidationException("Either accountTypeId or acctTypeCode is required for new account");
            if (na.getBranchId() == null && (na.getBranchCode() == null || na.getBranchCode().isBlank()))
                throw new BusinessValidationException("Either branchId or branchCode is required for new account");
            String acctStatus = na.getAcctStatusCode() != null && !na.getAcctStatusCode().isBlank() ? na.getAcctStatusCode().trim() : DEFAULT_ACCOUNT_STATUS;
            accountEligibilityService.requireStatusAndTypeAllowLinking(
                na.getAccountStatusId(), acctStatus, na.getAccountTypeId(), na.getAcctTypeCode());
            Long accountTypeId = na.getAccountTypeId() != null ? accountTypeRepository.findById(na.getAccountTypeId()).orElseThrow(() -> new ResourceNotFoundException("AccountType", String.valueOf(na.getAccountTypeId()))).getId()
                : accountTypeRepository.findByAcctTypeCode(na.getAcctTypeCode()).orElseThrow(() -> new ResourceNotFoundException("AccountType", na.getAcctTypeCode())).getId();
            Long accountStatusId = na.getAccountStatusId() != null ? accountStatusRepository.findById(na.getAccountStatusId()).orElseThrow(() -> new ResourceNotFoundException("AccountStatus", String.valueOf(na.getAccountStatusId()))).getId()
                : accountStatusRepository.findByAcctStatusCode(acctStatus).orElseThrow(() -> new ResourceNotFoundException("AccountStatus", acctStatus)).getId();
            Long branchId = na.getBranchId() != null ? branchRepository.findById(na.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", String.valueOf(na.getBranchId()))).getId()
                : branchRepository.findByBranchCode(na.getBranchCode()).orElseThrow(() -> new ResourceNotFoundException("Branch", na.getBranchCode())).getId();
            Account account = new Account();
            account.setAccountNum(num);
            account.setAccountTitle(na.getAccountTitle());
            account.setAccountTypeId(accountTypeId);
            accountTypeRepository.findById(accountTypeId).ifPresent(at -> account.setAcctTypeCode(at.getAcctTypeCode()));
            account.setAccountStatusId(accountStatusId);
            accountStatusRepository.findById(accountStatusId).ifPresent(as -> account.setAcctStatusCode(as.getAcctStatusCode()));
            account.setBranchId(branchId);
            branchRepository.findById(branchId).ifPresent(b -> account.setBranchCode(b.getBranchCode()));
            String by = createdBy != null ? createdBy : "system";
            account.setOpenedDate(LocalDateTime.now());
            account.setCreatedOn(LocalDateTime.now());
            account.setCreatedBy(by);
            account.setUpdatedOn(LocalDateTime.now());
            account.setUpdatedBy(by);
            account.setIsClosed(Boolean.FALSE);
            accountRepository.save(account);
            return num;
        }
        String existing = request.getAccountNum();
        if (existing == null || existing.isBlank())
            throw new BusinessValidationException("accountNum is required when not creating a new account");
        accountEligibilityService.requireEligibleForCardOrLink(existing);
        return existing;
    }

    /**
     * Prefer explicit requestTypeId. If blank and requestSource is MOBILE, default to NEW
     * so portal approve/generate assigns the standard limit profile.
     */
    private String resolveRequestTypeId(NewCardRequestCreate request) {
        if (request.getRequestTypeId() != null && !request.getRequestTypeId().isBlank()) {
            return request.getRequestTypeId().trim();
        }
        if (request.getRequestSource() != null && "MOBILE".equalsIgnoreCase(request.getRequestSource().trim())) {
            return "NEW";
        }
        return request.getRequestTypeId();
    }

    @Override
    @Transactional
    public void reject(Long requestId) {
        CardRequest cr = cardRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        cr.setProgressFlag(-1);
        cr.setIsProcessed(1);
        cr.setUpdatedOn(LocalDateTime.now());
        cardRequestRepository.save(cr);
    }

    @Override
    public List<CardRequestResponse> getCheckerList() {
        return enrichAll(cardRequestMapper.toResponseList(cardRequestRepository.findByProgressFlag(0)));
    }

    @Override
    public List<CardRequestResponse> getMakerList() {
        return enrichAll(cardRequestMapper.toResponseList(cardRequestRepository.findByIsProcessed(0)));
    }

    @Override
    @Transactional
    public CardRequestResponse update(Long requestId, NewCardRequestCreate request) {
        CardRequest cr = cardRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        if (request.getCardTitle() != null) cr.setCardTitle(normalizeCardTitle(request.getCardTitle()));
        if (request.getCardTypeId() != null) cardTypeRepository.findById(request.getCardTypeId()).ifPresent(t -> { cr.setCardTypeCode(t.getCardTypeCode()); });
        if (request.getCardTypeCode() != null && request.getCardTypeId() == null) cardTypeRepository.findByCardTypeCode(request.getCardTypeCode()).ifPresent(t -> { cr.setCardTypeCode(t.getCardTypeCode()); });
        if (request.getProductId() != null) cardProductRepository.findById(request.getProductId()).ifPresent(p -> { cr.setProductCode(p.getProductCode()); });
        if (request.getProductCode() != null && request.getProductId() == null) cardProductRepository.findByProductCode(request.getProductCode()).ifPresent(p -> { cr.setProductCode(p.getProductCode()); });
        if (request.getBranchId() != null) branchRepository.findById(request.getBranchId()).ifPresent(b -> { cr.setBranchCode(b.getBranchCode()); });
        if (request.getBranchCode() != null && request.getBranchId() == null) branchRepository.findByBranchCode(request.getBranchCode()).ifPresent(b -> { cr.setBranchCode(b.getBranchCode()); });
        if (request.getSupplementaryCount() != null) cr.setSupplementaryCount(request.getSupplementaryCount());
        cr.setUpdatedOn(LocalDateTime.now());
        return enrich(cardRequestMapper.toResponse(cardRequestRepository.save(cr)));
    }

    @Override
    public CustomerInfoResponse getCustomerInfo(String relationshipNum) {
        CustomerInfoResponse r = new CustomerInfoResponse();
        r.setRelationshipNum(relationshipNum);
        return r;
    }

    @Override
    public PageResponse<CardRequestResponse> search(String relationshipNum, String branchCode, Integer isProcessed,
                                                    String requestTypeId, Integer page, Integer size) {
        var pageable = PageRequest.of(page != null ? page : 0, size != null && size > 0 ? size : 20);
        String rel = blankToNull(relationshipNum);
        String branch = blankToNull(branchCode);
        String type = blankToNull(requestTypeId);
        var pageResult = cardRequestRepository.search(rel, branch, isProcessed, type, pageable);
        PageResponse<CardRequestResponse> pr = new PageResponse<>();
        pr.setContent(enrichAll(cardRequestMapper.toResponseList(pageResult.getContent())));
        pr.setPage(pageResult.getNumber());
        pr.setSize(pageResult.getSize());
        pr.setTotalElements(pageResult.getTotalElements());
        pr.setTotalPages(pageResult.getTotalPages());
        return pr;
    }

    @Override
    public CardRequestResponse getById(Long requestId) {
        CardRequest cr = cardRequestRepository.findByRequestIdWithDetails(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("CardRequest", String.valueOf(requestId)));
        return enrich(cardRequestMapper.toResponse(cr));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private CardRequestResponse enrich(CardRequestResponse response) {
        if (response == null) return null;
        enrichAll(List.of(response));
        return response;
    }

    /** Resolve type / product / branch display names from codes. */
    private List<CardRequestResponse> enrichAll(List<CardRequestResponse> list) {
        if (list == null || list.isEmpty()) return list;
        Set<String> typeCodes = new HashSet<>();
        Set<String> productCodes = new HashSet<>();
        Set<String> branchCodes = new HashSet<>();
        for (CardRequestResponse r : list) {
            if (r.getCardTypeName() == null && r.getCardTypeCode() != null && !r.getCardTypeCode().isBlank()) {
                typeCodes.add(r.getCardTypeCode());
            }
            if (r.getProductName() == null && r.getProductCode() != null && !r.getProductCode().isBlank()) {
                productCodes.add(r.getProductCode());
            }
            if (r.getBranchName() == null && r.getBranchCode() != null && !r.getBranchCode().isBlank()) {
                branchCodes.add(r.getBranchCode());
            }
        }
        Map<String, String> typeNames = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();
        Map<String, String> branchNames = new HashMap<>();
        for (String code : typeCodes) {
            cardTypeRepository.findByCardTypeCode(code).ifPresent(t -> typeNames.put(code, t.getCardTypeName()));
        }
        for (String code : productCodes) {
            cardProductRepository.findByProductCode(code).ifPresent(p -> productNames.put(code, p.getProductName()));
        }
        for (String code : branchCodes) {
            branchRepository.findByBranchCode(code).ifPresent(b -> branchNames.put(code, b.getBranchName()));
        }
        for (CardRequestResponse r : list) {
            if (r.getCardTypeName() == null && r.getCardTypeCode() != null) {
                r.setCardTypeName(typeNames.get(r.getCardTypeCode()));
            }
            if (r.getProductName() == null && r.getProductCode() != null) {
                r.setProductName(productNames.get(r.getProductCode()));
            }
            if (r.getBranchName() == null && r.getBranchCode() != null) {
                r.setBranchName(branchNames.get(r.getBranchCode()));
            }
        }
        return list;
    }

    private String normalizeCardTitle(String cardTitle) {
        if (cardTitle == null || cardTitle.isBlank()) {
            throw new BusinessValidationException("cardTitle is required");
        }
        String trimmed = cardTitle.trim();
        for (int i = 0; i < trimmed.length(); i++) {
            if (Character.isLowerCase(trimmed.charAt(i))) {
                throw new BusinessValidationException("cardTitle must not contain lowercase letters");
            }
        }
        return trimmed.toUpperCase();
    }
}
