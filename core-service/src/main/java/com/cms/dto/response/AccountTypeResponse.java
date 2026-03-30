package com.cms.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountTypeResponse {
    private Long id;
    private String acctTypeCode;
    private String acctTypeName;
    private Boolean isFrom;
    private Boolean isTo;
    private String isoCode;
    private String groupId;
    private BigDecimal isLinkingAllowed;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
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
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public BigDecimal getIsLinkingAllowed() { return isLinkingAllowed; }
    public void setIsLinkingAllowed(BigDecimal isLinkingAllowed) { this.isLinkingAllowed = isLinkingAllowed; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
