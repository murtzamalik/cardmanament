package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "USM_GROUP")
public class UsmGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usm_group_seq_gen")
    @SequenceGenerator(name = "usm_group_seq_gen", sequenceName = "USM_GROUP_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "GROUP_NAME", length = 255)
    private String groupName;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "IS_ACTIVE")
    private BigDecimal isActive;

    @Column(name = "WHEN_DELETED")
    private LocalDateTime whenDeleted;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "HOME_PAGE", length = 255)
    private String homePage;

    @Column(name = "IS_ADMIN")
    private Boolean isAdmin;

    @Column(name = "FA_OPTION_ID", length = 50)
    private String faOptionId;

    @Column(name = "APP_ID", length = 50)
    private String appId;

    @Column(name = "CORPORATE_CODE", length = 50)
    private String corporateCode;

    @Column(name = "MERCHANT_CODE", length = 50)
    private String merchantCode;

    @Column(name = "STORE_CODE", length = 50)
    private String storeCode;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public BigDecimal getIsActive() { return isActive; }
    public void setIsActive(BigDecimal isActive) { this.isActive = isActive; }
    public LocalDateTime getWhenDeleted() { return whenDeleted; }
    public void setWhenDeleted(LocalDateTime whenDeleted) { this.whenDeleted = whenDeleted; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getHomePage() { return homePage; }
    public void setHomePage(String homePage) { this.homePage = homePage; }
    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    public String getFaOptionId() { return faOptionId; }
    public void setFaOptionId(String faOptionId) { this.faOptionId = faOptionId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getCorporateCode() { return corporateCode; }
    public void setCorporateCode(String corporateCode) { this.corporateCode = corporateCode; }
    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
    public String getStoreCode() { return storeCode; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
