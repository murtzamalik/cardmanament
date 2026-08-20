package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LIMIT_PROFILE")
public class LimitProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "limit_profile_seq_gen")
    @SequenceGenerator(name = "limit_profile_seq_gen", sequenceName = "LIMIT_PROFILE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROFILE_CODE", length = 50)
    private String profileCode;

    @Column(name = "PROFILE_NAME", length = 255)
    private String profileName;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    @Column(name = "ATM_DAILY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal atmDailyAmount;

    @Column(name = "ATM_MONTHLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal atmMonthlyAmount;

    @Column(name = "ATM_YEARLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal atmYearlyAmount;

    @Column(name = "POS_DAILY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal posDailyAmount;

    @Column(name = "POS_MONTHLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal posMonthlyAmount;

    @Column(name = "POS_YEARLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal posYearlyAmount;

    @Column(name = "ECOMMERCE_DAILY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal ecommerceDailyAmount;

    @Column(name = "ECOMMERCE_MONTHLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal ecommerceMonthlyAmount;

    @Column(name = "ECOMMERCE_YEARLY_AMOUNT", precision = 18, scale = 2)
    private BigDecimal ecommerceYearlyAmount;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    public String getProfileCode() { return profileCode; }
    public void setProfileCode(String profileCode) { this.profileCode = profileCode; }
    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public BigDecimal getAtmDailyAmount() { return atmDailyAmount; }
    public void setAtmDailyAmount(BigDecimal atmDailyAmount) { this.atmDailyAmount = atmDailyAmount; }
    public BigDecimal getAtmMonthlyAmount() { return atmMonthlyAmount; }
    public void setAtmMonthlyAmount(BigDecimal atmMonthlyAmount) { this.atmMonthlyAmount = atmMonthlyAmount; }
    public BigDecimal getAtmYearlyAmount() { return atmYearlyAmount; }
    public void setAtmYearlyAmount(BigDecimal atmYearlyAmount) { this.atmYearlyAmount = atmYearlyAmount; }
    public BigDecimal getPosDailyAmount() { return posDailyAmount; }
    public void setPosDailyAmount(BigDecimal posDailyAmount) { this.posDailyAmount = posDailyAmount; }
    public BigDecimal getPosMonthlyAmount() { return posMonthlyAmount; }
    public void setPosMonthlyAmount(BigDecimal posMonthlyAmount) { this.posMonthlyAmount = posMonthlyAmount; }
    public BigDecimal getPosYearlyAmount() { return posYearlyAmount; }
    public void setPosYearlyAmount(BigDecimal posYearlyAmount) { this.posYearlyAmount = posYearlyAmount; }
    public BigDecimal getEcommerceDailyAmount() { return ecommerceDailyAmount; }
    public void setEcommerceDailyAmount(BigDecimal ecommerceDailyAmount) { this.ecommerceDailyAmount = ecommerceDailyAmount; }
    public BigDecimal getEcommerceMonthlyAmount() { return ecommerceMonthlyAmount; }
    public void setEcommerceMonthlyAmount(BigDecimal ecommerceMonthlyAmount) { this.ecommerceMonthlyAmount = ecommerceMonthlyAmount; }
    public BigDecimal getEcommerceYearlyAmount() { return ecommerceYearlyAmount; }
    public void setEcommerceYearlyAmount(BigDecimal ecommerceYearlyAmount) { this.ecommerceYearlyAmount = ecommerceYearlyAmount; }
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
