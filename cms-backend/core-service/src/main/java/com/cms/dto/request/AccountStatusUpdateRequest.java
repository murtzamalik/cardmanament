package com.cms.dto.request;


import java.math.BigDecimal;

public class AccountStatusUpdateRequest {
    private String acctStatusName;
    private String description;
    private BigDecimal isTranAllowed;
    private BigDecimal isLinkingAllowed;
    private String isoCode;
    private BigDecimal isActive;
    private String groupId;
    private String mappingId;
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
}
