package com.cms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class NewCardRequestCreate {
    @NotBlank(message = "relationshipNum is required")
    private String relationshipNum;
    /** Required when newAccount is null; ignored when newAccount is present. */
    private String accountNum;
    /** When present, backend creates the account first then uses its accountNum for the card request. */
    @Valid
    private NewAccountRequest newAccount;
    private String cardTitle;
    /** Preferred: use cardTypeId. When null, cardTypeCode is used to resolve. */
    private Long cardTypeId;
    private String cardTypeCode;
    private Long productId;
    private String productCode;
    private Long branchId;
    private String branchCode;
    private Integer supplementaryCount = 0;
    private String requestTypeId;

    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public Integer getSupplementaryCount() { return supplementaryCount; }
    public void setSupplementaryCount(Integer supplementaryCount) { this.supplementaryCount = supplementaryCount; }
    public String getRequestTypeId() { return requestTypeId; }
    public void setRequestTypeId(String requestTypeId) { this.requestTypeId = requestTypeId; }
    public NewAccountRequest getNewAccount() { return newAccount; }
    public void setNewAccount(NewAccountRequest newAccount) { this.newAccount = newAccount; }
}
