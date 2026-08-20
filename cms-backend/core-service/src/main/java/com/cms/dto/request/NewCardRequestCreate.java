package com.cms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class NewCardRequestCreate {
    @NotBlank(message = "relationshipNum is required")
    private String relationshipNum;
    /** Required when newAccount is null; ignored when newAccount is present. */
    private String accountNum;
    /** When present, backend creates the account first then uses its accountNum for the card request. */
    @Valid
    private NewAccountRequest newAccount;
    @NotBlank(message = "cardTitle is required")
    @Pattern(regexp = "^[^a-z]*$", message = "cardTitle must not contain lowercase letters")
    private String cardTitle;
    /** Preferred: use cardTypeId. When null, cardTypeCode is used to resolve. */
    private Long cardTypeId;
    private String cardTypeCode;
    private Long productId;
    private String productCode;
    private Long branchId;
    private String branchCode;
    private Integer supplementaryCount = 0;
    /**
     * Request type from LOV (e.g. NEW / 1 for mobile new-card).
     * On approve/generate, limit comes from card type defaultLimitProfileId (fallback STD).
     */
    private String requestTypeId;
    /**
     * Optional origin of the request. When set to MOBILE and requestTypeId is blank, defaults to NEW
     * so approve/generate applies the standard limit profile.
     */
    private String requestSource;
    /** Source card id for CHANGE_TYPE / REPLACEMENT flows. */
    private Long sourceCardId;

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
    public String getRequestSource() { return requestSource; }
    public void setRequestSource(String requestSource) { this.requestSource = requestSource; }
    public NewAccountRequest getNewAccount() { return newAccount; }
    public void setNewAccount(NewAccountRequest newAccount) { this.newAccount = newAccount; }
    public Long getSourceCardId() { return sourceCardId; }
    public void setSourceCardId(Long sourceCardId) { this.sourceCardId = sourceCardId; }
}
