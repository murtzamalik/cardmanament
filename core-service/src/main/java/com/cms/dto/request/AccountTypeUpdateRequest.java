package com.cms.dto.request;


import java.math.BigDecimal;

public class AccountTypeUpdateRequest {
    private String acctTypeName;
    private Boolean isFrom;
    private Boolean isTo;
    private String isoCode;
    private String groupId;
    private BigDecimal isLinkingAllowed;
    public String getAcctTypeName() { return acctTypeName; }
    public void setAcctTypeName(String acctTypeName) { this.acctTypeName = acctTypeName; }
    public Boolean getIsFrom() { return isFrom; }
    public void setIsFrom(Boolean isFrom) { this.isFrom = isFrom; }
    public Boolean getIsTo() { return isTo; }
    public void setIsTo(Boolean isTo) { this.isTo = isTo; }
    public String getIsoCode() { return isoCode; }
    public void setIsoCode(String isoCode) { this.isoCode = isoCode; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public BigDecimal getIsLinkingAllowed() { return isLinkingAllowed; }
    public void setIsLinkingAllowed(BigDecimal isLinkingAllowed) { this.isLinkingAllowed = isLinkingAllowed; }
}
