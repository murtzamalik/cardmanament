package com.cms.service;

import com.cms.dal.entity.Account;
import com.cms.dal.entity.AccountStatus;
import com.cms.dal.entity.AccountType;
import com.cms.dal.repository.AccountRepository;
import com.cms.dal.repository.AccountStatusRepository;
import com.cms.dal.repository.AccountTypeRepository;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Shared rules for whether an account may be used for card request, generation, or linking.
 */
@Service
public class AccountEligibilityService {

    private static final String CLOSED_STATUS = "CLOSED";

    private final AccountRepository accountRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final AccountTypeRepository accountTypeRepository;

    public AccountEligibilityService(AccountRepository accountRepository,
                                     AccountStatusRepository accountStatusRepository,
                                     AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.accountStatusRepository = accountStatusRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    public void requireEligibleForCardOrLink(String accountNum) {
        Account account = accountRepository.findByAccountNum(accountNum)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountNum));
        requireEligibleForCardOrLink(account);
    }

    public void requireEligibleForCardOrLink(Account account) {
        if (account == null) {
            throw new BusinessValidationException("Account is required");
        }
        String accountNum = account.getAccountNum() != null ? account.getAccountNum() : "";

        if (Boolean.TRUE.equals(account.getIsClosed())) {
            throw new BusinessValidationException(
                "Card cannot be generated or linked for a closed account: " + accountNum);
        }

        if (isClosedStatusCode(account.getAcctStatusCode())) {
            throw new BusinessValidationException(
                "Card cannot be generated or linked for a closed account: " + accountNum);
        }

        Optional<AccountStatus> status = resolveAccountStatus(account);
        if (status.isPresent()) {
            AccountStatus as = status.get();
            if (isClosedStatusCode(as.getAcctStatusCode())) {
                throw new BusinessValidationException(
                    "Card cannot be generated or linked for a closed account: " + accountNum);
            }
            if (isFlagDisabled(as.getIsLinkingAllowed())) {
                throw new BusinessValidationException(
                    "Account status does not allow card linking for account: " + accountNum);
            }
        }

        Optional<AccountType> type = resolveAccountType(account);
        if (type.isPresent() && isFlagDisabled(type.get().getIsLinkingAllowed())) {
            throw new BusinessValidationException(
                "Account type does not allow card linking for account: " + accountNum);
        }
    }

    /**
     * Validates status/type when creating a new account as part of a card request.
     */
    public void requireStatusAndTypeAllowLinking(Long accountStatusId, String acctStatusCode,
                                                 Long accountTypeId, String acctTypeCode) {
        AccountStatus status = null;
        if (accountStatusId != null) {
            status = accountStatusRepository.findById(accountStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountStatus", String.valueOf(accountStatusId)));
        } else if (acctStatusCode != null && !acctStatusCode.isBlank()) {
            status = accountStatusRepository.findByAcctStatusCode(acctStatusCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("AccountStatus", acctStatusCode));
        }
        if (status != null) {
            if (isClosedStatusCode(status.getAcctStatusCode())) {
                throw new BusinessValidationException(
                    "Cannot create a card request for an account with closed status");
            }
            if (isFlagDisabled(status.getIsLinkingAllowed())) {
                throw new BusinessValidationException(
                    "Selected account status does not allow card linking");
            }
        } else if (isClosedStatusCode(acctStatusCode)) {
            throw new BusinessValidationException(
                "Cannot create a card request for an account with closed status");
        }

        AccountType type = null;
        if (accountTypeId != null) {
            type = accountTypeRepository.findById(accountTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountType", String.valueOf(accountTypeId)));
        } else if (acctTypeCode != null && !acctTypeCode.isBlank()) {
            type = accountTypeRepository.findByAcctTypeCode(acctTypeCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("AccountType", acctTypeCode));
        }
        if (type != null && isFlagDisabled(type.getIsLinkingAllowed())) {
            throw new BusinessValidationException(
                "Selected account type does not allow card linking");
        }
    }

    public boolean isEligibleForCardOrLink(Account account) {
        if (account == null) return false;
        try {
            requireEligibleForCardOrLink(account);
            return true;
        } catch (BusinessValidationException ex) {
            return false;
        }
    }

    private Optional<AccountStatus> resolveAccountStatus(Account account) {
        if (account.getAccountStatusId() != null) {
            return accountStatusRepository.findById(account.getAccountStatusId());
        }
        if (account.getAcctStatusCode() != null && !account.getAcctStatusCode().isBlank()) {
            return accountStatusRepository.findByAcctStatusCode(account.getAcctStatusCode().trim());
        }
        return Optional.empty();
    }

    private Optional<AccountType> resolveAccountType(Account account) {
        if (account.getAccountTypeId() != null) {
            return accountTypeRepository.findById(account.getAccountTypeId());
        }
        if (account.getAcctTypeCode() != null && !account.getAcctTypeCode().isBlank()) {
            return accountTypeRepository.findByAcctTypeCode(account.getAcctTypeCode().trim());
        }
        return Optional.empty();
    }

    private static boolean isClosedStatusCode(String code) {
        return code != null && CLOSED_STATUS.equalsIgnoreCase(code.trim());
    }

    /** Explicit 0 means disabled; null is treated as allowed (legacy rows). */
    private static boolean isFlagDisabled(BigDecimal flag) {
        return flag != null && flag.compareTo(BigDecimal.ZERO) == 0;
    }
}
