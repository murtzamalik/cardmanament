package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_gen")
    @SequenceGenerator(name = "account_seq_gen", sequenceName = "ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "ACCOUNT_NUM", length = 50)
    private String accountNum;

    @Column(name = "ACCOUNT_TITLE", length = 255)
    private String accountTitle;

    @Column(name = "ACCT_TYPE_CODE", length = 50)
    private String acctTypeCode;

    @Column(name = "ACCT_STATUS_CODE", length = 50)
    private String acctStatusCode;

    @Column(name = "BRANCH_CODE", length = 50)
    private String branchCode;

    @Column(name = "ACCOUNT_TYPE_ID")
    private Long accountTypeId;

    @Column(name = "ACCOUNT_STATUS_ID")
    private Long accountStatusId;

    @Column(name = "BRANCH_ID")
    private Long branchId;

    @Column(name = "LIMIT_PROFILE_ID")
    private Long limitProfileId;

    @Column(name = "OPENED_DATE")
    private LocalDateTime openedDate;

    @Column(name = "LIMIT_PROFILE", length = 50)
    private String limitProfile;

    @Column(name = "CURRENCY_CODE", length = 20)
    private String currencyCode;

    @Column(name = "IS_JOINT")
    private BigDecimal isJoint;

    @Column(name = "CLOSED_DATE")
    private LocalDateTime closedDate;

    @Column(name = "CLOSED_REMARKS", length = 500)
    private String closedRemarks;

    @Column(name = "RESERVER_4", length = 255)
    private String reserver4;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "IBAN", length = 100)
    private String iban;

    @Column(name = "IS_CLOSED")
    private Boolean isClosed;

    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getAccountTitle() { return accountTitle; }
    public void setAccountTitle(String accountTitle) { this.accountTitle = accountTitle; }
    public String getAcctTypeCode() { return acctTypeCode; }
    public void setAcctTypeCode(String acctTypeCode) { this.acctTypeCode = acctTypeCode; }
    public String getAcctStatusCode() { return acctStatusCode; }
    public void setAcctStatusCode(String acctStatusCode) { this.acctStatusCode = acctStatusCode; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public LocalDateTime getOpenedDate() { return openedDate; }
    public void setOpenedDate(LocalDateTime openedDate) { this.openedDate = openedDate; }
    public String getLimitProfile() { return limitProfile; }
    public void setLimitProfile(String limitProfile) { this.limitProfile = limitProfile; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public BigDecimal getIsJoint() { return isJoint; }
    public void setIsJoint(BigDecimal isJoint) { this.isJoint = isJoint; }
    public LocalDateTime getClosedDate() { return closedDate; }
    public void setClosedDate(LocalDateTime closedDate) { this.closedDate = closedDate; }
    public String getClosedRemarks() { return closedRemarks; }
    public void setClosedRemarks(String closedRemarks) { this.closedRemarks = closedRemarks; }
    public String getReserver4() { return reserver4; }
    public void setReserver4(String reserver4) { this.reserver4 = reserver4; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Long getAccountTypeId() { return accountTypeId; }
    public void setAccountTypeId(Long accountTypeId) { this.accountTypeId = accountTypeId; }
    public Long getAccountStatusId() { return accountStatusId; }
    public void setAccountStatusId(Long accountStatusId) { this.accountStatusId = accountStatusId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public Long getLimitProfileId() { return limitProfileId; }
    public void setLimitProfileId(Long limitProfileId) { this.limitProfileId = limitProfileId; }
}
