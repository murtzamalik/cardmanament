package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class LimitProfileCreateRequest {
    @NotBlank private String profileCode;
    private String profileName;
    private String currencyCode;
    private BigDecimal atmDailyAmount;
    private BigDecimal atmMonthlyAmount;
    private BigDecimal atmYearlyAmount;
    private BigDecimal posDailyAmount;
    private BigDecimal posMonthlyAmount;
    private BigDecimal posYearlyAmount;
    private BigDecimal ecommerceDailyAmount;
    private BigDecimal ecommerceMonthlyAmount;
    private BigDecimal ecommerceYearlyAmount;
    private Boolean active;

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
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
