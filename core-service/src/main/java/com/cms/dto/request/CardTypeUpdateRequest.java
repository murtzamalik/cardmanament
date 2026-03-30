package com.cms.dto.request;

public class CardTypeUpdateRequest {

    private String cardTypeName;
    /** Preferred; when null, productCode is used. */
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
}
