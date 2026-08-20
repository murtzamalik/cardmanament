package com.cms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CardProductUpdateRequest {

    private String productName;
    @Min(value = 100000, message = "bin must be exactly 6 digits")
    @Max(value = 999999, message = "bin must be exactly 6 digits")
    private Integer bin;
    private Boolean isActive;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
