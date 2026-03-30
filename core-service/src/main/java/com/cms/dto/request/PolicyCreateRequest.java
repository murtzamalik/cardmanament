package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class PolicyCreateRequest {
    @NotBlank private String policyId;
    private String policyName;
    private String policyDescription;
    private String timeExpression;
    private BigDecimal isAutoReset;
    private BigDecimal isMultiLogin;
    private BigDecimal isDefault;
    private BigDecimal pwdExpiryPeriod;
    private BigDecimal pwdExpiryNotifyPeriod;
    private BigDecimal pwdRetryCount;
    private BigDecimal pwdHistoryCount;
    private BigDecimal accountDisablePeriod;
    private BigDecimal isFirstResetRequired;
    private BigDecimal isCyclicPwdAllowed;
    private String pwdExpId;
    private String passwordExpression;
    private Boolean canPwdMatchLogin;
    private Boolean isCommonWordAllowed;
    private Integer passwordExpiryCount;
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public String getPolicyDescription() { return policyDescription; }
    public void setPolicyDescription(String policyDescription) { this.policyDescription = policyDescription; }
    public String getTimeExpression() { return timeExpression; }
    public void setTimeExpression(String timeExpression) { this.timeExpression = timeExpression; }
    public BigDecimal getIsAutoReset() { return isAutoReset; }
    public void setIsAutoReset(BigDecimal isAutoReset) { this.isAutoReset = isAutoReset; }
    public BigDecimal getIsMultiLogin() { return isMultiLogin; }
    public void setIsMultiLogin(BigDecimal isMultiLogin) { this.isMultiLogin = isMultiLogin; }
    public BigDecimal getIsDefault() { return isDefault; }
    public void setIsDefault(BigDecimal isDefault) { this.isDefault = isDefault; }
    public BigDecimal getPwdExpiryPeriod() { return pwdExpiryPeriod; }
    public void setPwdExpiryPeriod(BigDecimal pwdExpiryPeriod) { this.pwdExpiryPeriod = pwdExpiryPeriod; }
    public BigDecimal getPwdExpiryNotifyPeriod() { return pwdExpiryNotifyPeriod; }
    public void setPwdExpiryNotifyPeriod(BigDecimal pwdExpiryNotifyPeriod) { this.pwdExpiryNotifyPeriod = pwdExpiryNotifyPeriod; }
    public BigDecimal getPwdRetryCount() { return pwdRetryCount; }
    public void setPwdRetryCount(BigDecimal pwdRetryCount) { this.pwdRetryCount = pwdRetryCount; }
    public BigDecimal getPwdHistoryCount() { return pwdHistoryCount; }
    public void setPwdHistoryCount(BigDecimal pwdHistoryCount) { this.pwdHistoryCount = pwdHistoryCount; }
    public BigDecimal getAccountDisablePeriod() { return accountDisablePeriod; }
    public void setAccountDisablePeriod(BigDecimal accountDisablePeriod) { this.accountDisablePeriod = accountDisablePeriod; }
    public BigDecimal getIsFirstResetRequired() { return isFirstResetRequired; }
    public void setIsFirstResetRequired(BigDecimal isFirstResetRequired) { this.isFirstResetRequired = isFirstResetRequired; }
    public BigDecimal getIsCyclicPwdAllowed() { return isCyclicPwdAllowed; }
    public void setIsCyclicPwdAllowed(BigDecimal isCyclicPwdAllowed) { this.isCyclicPwdAllowed = isCyclicPwdAllowed; }
    public String getPwdExpId() { return pwdExpId; }
    public void setPwdExpId(String pwdExpId) { this.pwdExpId = pwdExpId; }
    public String getPasswordExpression() { return passwordExpression; }
    public void setPasswordExpression(String passwordExpression) { this.passwordExpression = passwordExpression; }
    public Boolean getCanPwdMatchLogin() { return canPwdMatchLogin; }
    public void setCanPwdMatchLogin(Boolean canPwdMatchLogin) { this.canPwdMatchLogin = canPwdMatchLogin; }
    public Boolean getIsCommonWordAllowed() { return isCommonWordAllowed; }
    public void setIsCommonWordAllowed(Boolean isCommonWordAllowed) { this.isCommonWordAllowed = isCommonWordAllowed; }
    public Integer getPasswordExpiryCount() { return passwordExpiryCount; }
    public void setPasswordExpiryCount(Integer passwordExpiryCount) { this.passwordExpiryCount = passwordExpiryCount; }
}
