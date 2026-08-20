package com.cms.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PolicyResponse {
    private Long id;
    private String policyId;
    private String policyName;
    private String policyDescription;
    private String timeExpression;
    private BigDecimal isAutoReset;
    private BigDecimal isMultiLogin;
    private BigDecimal isDefault;
    private BigDecimal pwdExpiryPeriod;
    private BigDecimal pwdRetryCount;
    private BigDecimal pwdHistoryCount;
    private String pwdExpId;
    private Boolean canPwdMatchLogin;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
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
    public BigDecimal getPwdRetryCount() { return pwdRetryCount; }
    public void setPwdRetryCount(BigDecimal pwdRetryCount) { this.pwdRetryCount = pwdRetryCount; }
    public BigDecimal getPwdHistoryCount() { return pwdHistoryCount; }
    public void setPwdHistoryCount(BigDecimal pwdHistoryCount) { this.pwdHistoryCount = pwdHistoryCount; }
    public String getPwdExpId() { return pwdExpId; }
    public void setPwdExpId(String pwdExpId) { this.pwdExpId = pwdExpId; }
    public Boolean getCanPwdMatchLogin() { return canPwdMatchLogin; }
    public void setCanPwdMatchLogin(Boolean canPwdMatchLogin) { this.canPwdMatchLogin = canPwdMatchLogin; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
