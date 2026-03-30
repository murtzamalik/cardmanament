package com.cms.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountStatusResponse {
    private Long id;
    private String acctStatusCode;
    private String acctStatusName;
    private String description;
    private BigDecimal isTranAllowed;
    private BigDecimal isLinkingAllowed;
    private String isoCode;
    private BigDecimal isActive;
    private String groupId;
    private String mappingId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
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
    public String getIsoCode() { return isoCode; }
    public void setIsoCode(String isoCode) { this.isoCode = isoCode; }
    public BigDecimal getIsActive() { return isActive; }
    public void setIsActive(BigDecimal isActive) { this.isActive = isActive; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
