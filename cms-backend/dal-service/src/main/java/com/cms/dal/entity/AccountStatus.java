package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNT_STATUS")
public class AccountStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_status_seq_gen")
    @SequenceGenerator(name = "account_status_seq_gen", sequenceName = "ACCOUNT_STATUS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCT_STATUS_CODE", length = 50)
    private String acctStatusCode;

    @Column(name = "ACCT_STATUS_NAME", length = 255)
    private String acctStatusName;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "IS_TRAN_ALLOWED")
    private BigDecimal isTranAllowed;

    @Column(name = "IS_LINKING_ALLOWED")
    private BigDecimal isLinkingAllowed;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "ISO_CODE", length = 20)
    private String isoCode;

    @Column(name = "IS_ACTIVE")
    private BigDecimal isActive;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "MAPPING_ID", length = 50)
    private String mappingId;

    @Column(name = "OTHER_LANG_NAME", length = 255)
    private String otherLangName;

    public String getAcctStatusCode() { return acctStatusCode; }
    public void setAcctStatusCode(String acctStatusCode) { this.acctStatusCode = acctStatusCode; }
    public String getAcctStatusName() { return acctStatusName; }
    public void setAcctStatusName(String acctStatusName) { this.acctStatusName = acctStatusName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getIsTranAllowed() { return isTranAllowed; }
    public void setIsTranAllowed(BigDecimal isTranAllowed) { this.isTranAllowed = isTranAllowed; }
    public BigDecimal getIsLinkingAllowed() { return isLinkingAllowed; }
    public void setIsLinkingAllowed(BigDecimal isLinkingAllowed) { this.isLinkingAllowed = isLinkingAllowed; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getIsoCode() { return isoCode; }
    public void setIsoCode(String isoCode) { this.isoCode = isoCode; }
    public BigDecimal getIsActive() { return isActive; }
    public void setIsActive(BigDecimal isActive) { this.isActive = isActive; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }
    public String getOtherLangName() { return otherLangName; }
    public void setOtherLangName(String otherLangName) { this.otherLangName = otherLangName; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
