package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNT_TYPE")
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_type_seq_gen")
    @SequenceGenerator(name = "account_type_seq_gen", sequenceName = "ACCOUNT_TYPE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCT_TYPE_CODE", length = 50)
    private String acctTypeCode;

    @Column(name = "ACCT_TYPE_NAME", length = 255)
    private String acctTypeName;

    @Column(name = "IS_FROM")
    private Boolean isFrom;

    @Column(name = "IS_TO")
    private Boolean isTo;

    @Column(name = "ISO_CODE", length = 20)
    private String isoCode;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "WINDOWS_CREATED_BY", length = 50)
    private String windowsCreatedBy;

    @Column(name = "WINDOWS_UPDATED_BY", length = 50)
    private String windowsUpdatedBy;

    @Column(name = "MACHINE_NAME", length = 255)
    private String machineName;

    @Column(name = "CORPORATE_ID", length = 50)
    private String corporateId;

    @Column(name = "IS_LINKING_ALLOWED")
    private BigDecimal isLinkingAllowed;

    public String getAcctTypeCode() { return acctTypeCode; }
    public void setAcctTypeCode(String acctTypeCode) { this.acctTypeCode = acctTypeCode; }
    public String getAcctTypeName() { return acctTypeName; }
    public void setAcctTypeName(String acctTypeName) { this.acctTypeName = acctTypeName; }
    public Boolean getIsFrom() { return isFrom; }
    public void setIsFrom(Boolean isFrom) { this.isFrom = isFrom; }
    public Boolean getIsTo() { return isTo; }
    public void setIsTo(Boolean isTo) { this.isTo = isTo; }
    public String getIsoCode() { return isoCode; }
    public void setIsoCode(String isoCode) { this.isoCode = isoCode; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getWindowsCreatedBy() { return windowsCreatedBy; }
    public void setWindowsCreatedBy(String windowsCreatedBy) { this.windowsCreatedBy = windowsCreatedBy; }
    public String getWindowsUpdatedBy() { return windowsUpdatedBy; }
    public void setWindowsUpdatedBy(String windowsUpdatedBy) { this.windowsUpdatedBy = windowsUpdatedBy; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public String getCorporateId() { return corporateId; }
    public void setCorporateId(String corporateId) { this.corporateId = corporateId; }
    public BigDecimal getIsLinkingAllowed() { return isLinkingAllowed; }
    public void setIsLinkingAllowed(BigDecimal isLinkingAllowed) { this.isLinkingAllowed = isLinkingAllowed; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
