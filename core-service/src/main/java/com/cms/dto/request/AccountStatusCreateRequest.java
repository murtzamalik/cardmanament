package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class AccountStatusCreateRequest {
    @NotBlank private String acctStatusCode;
    private String acctStatusName;
    private String description;
    private BigDecimal isTranAllowed;
    private BigDecimal isLinkingAllowed;
    private String isoCode;
    private String groupId;
    private String mappingId;
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
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }
}
