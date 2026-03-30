package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CardProductCreateRequest {

    @NotBlank(message = "productCode is required")
    private String productCode;

    private String productName;

    /** true = active, false = inactive. Default true if null. */
    private Boolean isActive;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
