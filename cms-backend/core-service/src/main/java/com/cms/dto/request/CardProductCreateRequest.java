package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CardProductCreateRequest {

    @NotBlank(message = "productCode is required")
    private String productCode;

    private String productName;

    @NotNull(message = "bin is required")
    @Min(value = 100000, message = "bin must be exactly 6 digits")
    @Max(value = 999999, message = "bin must be exactly 6 digits")
    private Integer bin;

    /** true = active, false = inactive. Default true if null. */
    private Boolean isActive;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
