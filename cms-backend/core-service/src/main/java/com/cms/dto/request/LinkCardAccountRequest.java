package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LinkCardAccountRequest {
    @NotBlank
    private String pan;
    @NotBlank
    private String accountNum;
    private String relationshipNum;
    private Boolean isOverallDefault;
    private Boolean isAcctTypeDefault;

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public Boolean getIsOverallDefault() { return isOverallDefault; }
    public void setIsOverallDefault(Boolean isOverallDefault) { this.isOverallDefault = isOverallDefault; }
    public Boolean getIsAcctTypeDefault() { return isAcctTypeDefault; }
    public void setIsAcctTypeDefault(Boolean isAcctTypeDefault) { this.isAcctTypeDefault = isAcctTypeDefault; }
}
