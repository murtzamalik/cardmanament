package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "USM_POLICY")
public class UsmPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_policy_seq_gen")
    @SequenceGenerator(name = "usm_policy_seq_gen", sequenceName = "USM_POLICY_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "POLICY_ID", length = 50)
    private String policyId;

    @Column(name = "POLICY_NAME", length = 255)
    private String policyName;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "POLICY_DESCRIPTION", length = 500)
    private String policyDescription;

    @Column(name = "TIME_EXPRESSION", length = 100)
    private String timeExpression;

    @Column(name = "IS_AUTO_RESET")
    private BigDecimal isAutoReset;

    @Column(name = "IS_MULTI_LOGIN")
    private BigDecimal isMultiLogin;

    @Column(name = "IS_DEFAULT")
    private BigDecimal isDefault;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "PWD_EXPIRY_PERIOD")
    private BigDecimal pwdExpiryPeriod;

    @Column(name = "PWD_EXPIRY_NOTIFY_PERIOD")
    private BigDecimal pwdExpiryNotifyPeriod;

    @Column(name = "PWD_RETRY_COUNT")
    private BigDecimal pwdRetryCount;

    @Column(name = "PWD_HISTORY_COUNT")
    private BigDecimal pwdHistoryCount;

    @Column(name = "ACCOUNT_DISABLE_PERIOD")
    private BigDecimal accountDisablePeriod;

    @Column(name = "IS_FIRST_RESET_REQUIRED")
    private BigDecimal isFirstResetRequired;

    @Column(name = "IS_CYCLIC_PWD_ALLOWED")
    private BigDecimal isCyclicPwdAllowed;

    @Column(name = "PWD_EXP_ID", length = 50)
    private String pwdExpId;

    @Column(name = "PASSWORD_EXPRESSION", length = 500)
    private String passwordExpression;

    @Column(name = "PWD_EXPRESSION_WEAK", length = 500)
    private String pwdExpressionWeak;

    @Column(name = "PWD_EXPRESSION_NORMAL", length = 500)
    private String pwdExpressionNormal;

    @Column(name = "PWD_EXPRESSION_STRONG", length = 500)
    private String pwdExpressionStrong;

    @Column(name = "CAN_PWD_MATCH_LOGIN")
    private Boolean canPwdMatchLogin;

    @Column(name = "IS_COMMON_WORD_ALLOWED")
    private Boolean isCommonWordAllowed;

    @Column(name = "PASSWORD_EXPIRY_COUNT")
    private Integer passwordExpiryCount;

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
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
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
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
    public String getPwdExpressionWeak() { return pwdExpressionWeak; }
    public void setPwdExpressionWeak(String pwdExpressionWeak) { this.pwdExpressionWeak = pwdExpressionWeak; }
    public String getPwdExpressionNormal() { return pwdExpressionNormal; }
    public void setPwdExpressionNormal(String pwdExpressionNormal) { this.pwdExpressionNormal = pwdExpressionNormal; }
    public String getPwdExpressionStrong() { return pwdExpressionStrong; }
    public void setPwdExpressionStrong(String pwdExpressionStrong) { this.pwdExpressionStrong = pwdExpressionStrong; }
    public Boolean getCanPwdMatchLogin() { return canPwdMatchLogin; }
    public void setCanPwdMatchLogin(Boolean canPwdMatchLogin) { this.canPwdMatchLogin = canPwdMatchLogin; }
    public Boolean getIsCommonWordAllowed() { return isCommonWordAllowed; }
    public void setIsCommonWordAllowed(Boolean isCommonWordAllowed) { this.isCommonWordAllowed = isCommonWordAllowed; }
    public Integer getPasswordExpiryCount() { return passwordExpiryCount; }
    public void setPasswordExpiryCount(Integer passwordExpiryCount) { this.passwordExpiryCount = passwordExpiryCount; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
