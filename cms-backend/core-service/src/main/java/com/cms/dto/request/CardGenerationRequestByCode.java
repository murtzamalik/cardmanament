package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CardGenerationRequestByCode {
    @NotBlank
    private String relationshipNum;
    @NotBlank
    private String accountNum;

    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
}
