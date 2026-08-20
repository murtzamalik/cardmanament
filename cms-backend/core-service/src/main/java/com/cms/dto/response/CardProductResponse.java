package com.cms.dto.response;

import java.time.LocalDateTime;

public class CardProductResponse {

    private Long id;
    private String productCode;
    private String productName;
    private Integer bin;
    private Boolean isActive;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
