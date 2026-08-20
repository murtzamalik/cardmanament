package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_PRODUCT")
public class CardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_product_seq_gen")
    @SequenceGenerator(name = "card_product_seq_gen", sequenceName = "CARD_PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRODUCT_CODE", length = 50)
    private String productCode;

    @Column(name = "PRODUCT_NAME", length = 255)
    private String productName;

    @Column(name = "BIN")
    private Integer bin;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
