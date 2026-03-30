package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BRANCH")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_seq_gen")
    @SequenceGenerator(name = "branch_seq_gen", sequenceName = "BRANCH_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BRANCH_CODE", length = 50)
    private String branchCode;

    @Column(name = "ENTITY_ID", length = 50)
    private String entityId;

    @Column(name = "SWIFT_CODE", length = 50)
    private String swiftCode;

    @Column(name = "BRANCH_NAME", length = 255)
    private String branchName;

    @Column(name = "CITY_CODE", length = 50)
    private String cityCode;

    @Column(name = "COUNTRY_CODE", length = 50)
    private String countryCode;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "BRIEF_NAME", length = 100)
    private String briefName;

    @Column(name = "ADDRESS_1", length = 500)
    private String address1;

    @Column(name = "ADDRESS_2", length = 500)
    private String address2;

    @Column(name = "ZIP_CODE", length = 20)
    private String zipCode;

    @Column(name = "PHONE1", length = 50)
    private String phone1;

    @Column(name = "PHONE2", length = 50)
    private String phone2;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CURRENCY_CODE", length = 20)
    private String currencyCode;

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public String getBriefName() { return briefName; }
    public void setBriefName(String briefName) { this.briefName = briefName; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getPhone1() { return phone1; }
    public void setPhone1(String phone1) { this.phone1 = phone1; }
    public String getPhone2() { return phone2; }
    public void setPhone2(String phone2) { this.phone2 = phone2; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
