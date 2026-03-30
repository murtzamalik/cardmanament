package com.cms.dto.request;

public class CardProductUpdateRequest {

    private String productName;
    private Boolean isActive;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
