package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "USM_USER")
public class UsmUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_user_seq_gen")
    @SequenceGenerator(name = "usm_user_seq_gen", sequenceName = "USM_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOGIN_ID", length = 50)
    private String loginId;

    @Column(name = "PASSWORD", length = 500)
    private String password;

    @Column(name = "FULL_NAME", length = 255)
    private String fullName;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "PREFERED_CULTURE", length = 20)
    private String preferedCulture;

    @Column(name = "IS_ACTIVE")
    private BigDecimal isActive;

    @Column(name = "WHEN_DELETED")
    private LocalDateTime whenDeleted;

    @Column(name = "POLICY_ID", length = 50)
    private String policyId;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "IS_LOGGED_IN")
    private BigDecimal isLoggedIn;

    @Column(name = "WINDOWS_LOGGEDIN_BY", length = 50)
    private String windowsLoggedinBy;

    @Column(name = "LAST_RESPONSE_AT")
    private LocalDateTime lastResponseAt;

    @Column(name = "IS_RESET_REQUIRED", length = 5)
    private String isResetRequired;

    @Column(name = "PWD_RETRY_COUNT")
    private BigDecimal pwdRetryCount;

    @Column(name = "SUPPORT_LEVEL_ID")
    private Integer supportLevelId;

    @Column(name = "EMAIL_ADDRESS", length = 255)
    private String emailAddress;

    @Column(name = "CORPORATE_ID", length = 50)
    private String corporateId;

    @Column(name = "APP_ID", length = 50)
    private String appId;

    @Column(name = "PWD_UPDATED_ON")
    private LocalDateTime pwdUpdatedOn;

    @Column(name = "RESET_PASSWORD")
    private BigDecimal resetPassword;

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public String getPreferedCulture() { return preferedCulture; }
    public void setPreferedCulture(String preferedCulture) { this.preferedCulture = preferedCulture; }
    public BigDecimal getIsActive() { return isActive; }
    public void setIsActive(BigDecimal isActive) { this.isActive = isActive; }
    public LocalDateTime getWhenDeleted() { return whenDeleted; }
    public void setWhenDeleted(LocalDateTime whenDeleted) { this.whenDeleted = whenDeleted; }
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public BigDecimal getIsLoggedIn() { return isLoggedIn; }
    public void setIsLoggedIn(BigDecimal isLoggedIn) { this.isLoggedIn = isLoggedIn; }
    public String getWindowsLoggedinBy() { return windowsLoggedinBy; }
    public void setWindowsLoggedinBy(String windowsLoggedinBy) { this.windowsLoggedinBy = windowsLoggedinBy; }
    public LocalDateTime getLastResponseAt() { return lastResponseAt; }
    public void setLastResponseAt(LocalDateTime lastResponseAt) { this.lastResponseAt = lastResponseAt; }
    public String getIsResetRequired() { return isResetRequired; }
    public void setIsResetRequired(String isResetRequired) { this.isResetRequired = isResetRequired; }
    public BigDecimal getPwdRetryCount() { return pwdRetryCount; }
    public void setPwdRetryCount(BigDecimal pwdRetryCount) { this.pwdRetryCount = pwdRetryCount; }
    public Integer getSupportLevelId() { return supportLevelId; }
    public void setSupportLevelId(Integer supportLevelId) { this.supportLevelId = supportLevelId; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getCorporateId() { return corporateId; }
    public void setCorporateId(String corporateId) { this.corporateId = corporateId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public LocalDateTime getPwdUpdatedOn() { return pwdUpdatedOn; }
    public void setPwdUpdatedOn(LocalDateTime pwdUpdatedOn) { this.pwdUpdatedOn = pwdUpdatedOn; }
    public BigDecimal getResetPassword() { return resetPassword; }
    public void setResetPassword(BigDecimal resetPassword) { this.resetPassword = resetPassword; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
