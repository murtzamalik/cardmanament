package com.cms.dto.response;

import java.time.LocalDateTime;

public class CardResponse {
    /** Alias for cardId; use for uniform id in API. */
    private Long id;
    private Long cardId;
    private String panMasked;
    private String relationshipNum;
    private String cardTitle;
    private LocalDateTime expiryDate;
    private String cardTypeCode;
    private String cardTypeName;
    private String cardStatusCode;
    private String cardStatusName;
    private String productCode;
    private String productName;
    private String branchCode;
    private String branchName;
    private String limitProfile;
    private LocalDateTime activationDate;
    private LocalDateTime issuedDate;
    private LocalDateTime createdOn;
    private String createdBy;
    private String exportFilePath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getPanMasked() { return panMasked; }
    public void setPanMasked(String panMasked) { this.panMasked = panMasked; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public String getCardTypeName() { return cardTypeName; }
    public void setCardTypeName(String cardTypeName) { this.cardTypeName = cardTypeName; }
    public String getCardStatusCode() { return cardStatusCode; }
    public void setCardStatusCode(String cardStatusCode) { this.cardStatusCode = cardStatusCode; }
    public String getCardStatusName() { return cardStatusName; }
    public void setCardStatusName(String cardStatusName) { this.cardStatusName = cardStatusName; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getLimitProfile() { return limitProfile; }
    public void setLimitProfile(String limitProfile) { this.limitProfile = limitProfile; }
    public LocalDateTime getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDateTime activationDate) { this.activationDate = activationDate; }
    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getExportFilePath() { return exportFilePath; }
    public void setExportFilePath(String exportFilePath) { this.exportFilePath = exportFilePath; }
}
