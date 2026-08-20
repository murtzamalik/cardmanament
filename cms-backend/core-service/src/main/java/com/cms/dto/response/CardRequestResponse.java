package com.cms.dto.response;

import java.time.LocalDateTime;

public class CardRequestResponse {
    /** Alias for requestId; use for uniform id in API. */
    private Long id;
    private Long requestId;
    private String relationshipNum;
    private String accountNum;
    private String cardTitle;
    private String cardTypeCode;
    private String cardTypeName;
    private String productCode;
    private String productName;
    private String branchCode;
    private String branchName;
    private Integer supplementaryCount;
    private Integer isProcessed;
    private Integer progressFlag;
    private String requestTypeId;
    private Long sourceCardId;
    private LocalDateTime createdOn;
    private String createdBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public String getCardTypeName() { return cardTypeName; }
    public void setCardTypeName(String cardTypeName) { this.cardTypeName = cardTypeName; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public Integer getSupplementaryCount() { return supplementaryCount; }
    public void setSupplementaryCount(Integer supplementaryCount) { this.supplementaryCount = supplementaryCount; }
    public Integer getIsProcessed() { return isProcessed; }
    public void setIsProcessed(Integer isProcessed) { this.isProcessed = isProcessed; }
    public Integer getProgressFlag() { return progressFlag; }
    public void setProgressFlag(Integer progressFlag) { this.progressFlag = progressFlag; }
    public String getRequestTypeId() { return requestTypeId; }
    public void setRequestTypeId(String requestTypeId) { this.requestTypeId = requestTypeId; }
    public Long getSourceCardId() { return sourceCardId; }
    public void setSourceCardId(Long sourceCardId) { this.sourceCardId = sourceCardId; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
