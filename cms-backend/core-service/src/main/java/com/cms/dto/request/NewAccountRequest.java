package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Nested DTO for creating a new account as part of a card request.
 * When present in NewCardRequestCreate, the backend creates the account first then the card request.
 */
public class NewAccountRequest {

    @NotBlank(message = "accountNum is required")
    private String accountNum;

    @NotBlank(message = "accountTitle is required")
    private String accountTitle;

    /** Preferred: use accountTypeId. When null, acctTypeCode is used. */
    private Long accountTypeId;
    private String acctTypeCode;
    private Long accountStatusId;
    /** Optional; defaults to OPEN if not set. */
    private String acctStatusCode;
    private Long branchId;
    private String branchCode;

    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getAccountTitle() { return accountTitle; }
    public void setAccountTitle(String accountTitle) { this.accountTitle = accountTitle; }
    public Long getAccountTypeId() { return accountTypeId; }
    public void setAccountTypeId(Long accountTypeId) { this.accountTypeId = accountTypeId; }
    public String getAcctTypeCode() { return acctTypeCode; }
    public void setAcctTypeCode(String acctTypeCode) { this.acctTypeCode = acctTypeCode; }
    public Long getAccountStatusId() { return accountStatusId; }
    public void setAccountStatusId(Long accountStatusId) { this.accountStatusId = accountStatusId; }
    public String getAcctStatusCode() { return acctStatusCode; }
    public void setAcctStatusCode(String acctStatusCode) { this.acctStatusCode = acctStatusCode; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
}
