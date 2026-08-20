package com.cms.dto.response;

import java.time.LocalDateTime;

public class CardTypeResponse {

    private Long id;
    private String cardTypeCode;
    private String cardTypeName;
    private Long productId;
    private String productCode;
    private Boolean isActive;
    private Long supplementaryAllowed;
    private Integer isSuppType;
    private String suppTypeCode;
    private Integer panLength;
    private Integer bin;
    private Integer expPeriod;
    private String panSequenceName;
    private Integer panSequenceLength;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Long defaultLimitProfileId;
    private String defaultLimitProfileCode;
    private String defaultLimitProfileName;

    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public String getCardTypeName() { return cardTypeName; }
    public void setCardTypeName(String cardTypeName) { this.cardTypeName = cardTypeName; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Long getSupplementaryAllowed() { return supplementaryAllowed; }
    public void setSupplementaryAllowed(Long supplementaryAllowed) { this.supplementaryAllowed = supplementaryAllowed; }
    public Integer getIsSuppType() { return isSuppType; }
    public void setIsSuppType(Integer isSuppType) { this.isSuppType = isSuppType; }
    public String getSuppTypeCode() { return suppTypeCode; }
    public void setSuppTypeCode(String suppTypeCode) { this.suppTypeCode = suppTypeCode; }
    public Integer getPanLength() { return panLength; }
    public void setPanLength(Integer panLength) { this.panLength = panLength; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public Integer getExpPeriod() { return expPeriod; }
    public void setExpPeriod(Integer expPeriod) { this.expPeriod = expPeriod; }
    public String getPanSequenceName() { return panSequenceName; }
    public void setPanSequenceName(String panSequenceName) { this.panSequenceName = panSequenceName; }
    public Integer getPanSequenceLength() { return panSequenceLength; }
    public void setPanSequenceLength(Integer panSequenceLength) { this.panSequenceLength = panSequenceLength; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDefaultLimitProfileId() { return defaultLimitProfileId; }
    public void setDefaultLimitProfileId(Long defaultLimitProfileId) { this.defaultLimitProfileId = defaultLimitProfileId; }
    public String getDefaultLimitProfileCode() { return defaultLimitProfileCode; }
    public void setDefaultLimitProfileCode(String defaultLimitProfileCode) { this.defaultLimitProfileCode = defaultLimitProfileCode; }
    public String getDefaultLimitProfileName() { return defaultLimitProfileName; }
    public void setDefaultLimitProfileName(String defaultLimitProfileName) { this.defaultLimitProfileName = defaultLimitProfileName; }
}
