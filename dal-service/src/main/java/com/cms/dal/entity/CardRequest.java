package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_REQUEST")
public class CardRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_request_seq_gen")
    @SequenceGenerator(name = "card_request_seq_gen", sequenceName = "CARD_REQUEST_SEQ", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "RELATIONSHIP_NUM", length = 50)
    private String relationshipNum;

    @Column(name = "ACCOUNT_NUM", length = 50)
    private String accountNum;

    @Column(name = "CARD_TITLE", length = 255)
    private String cardTitle;

    @Column(name = "CARD_TYPE_CODE", length = 50)
    private String cardTypeCode;

    @Column(name = "PRODUCT_CODE", length = 50)
    private String productCode;

    @Column(name = "BRANCH_CODE", length = 50)
    private String branchCode;

    @Column(name = "BRANCH_ID")
    private Long branchId;

    @Column(name = "CARD_TYPE_ID")
    private Long cardTypeId;

    @Column(name = "CARD_PRODUCT_ID")
    private Long cardProductId;

    @Column(name = "SUPPLEMENTARY_COUNT")
    private Integer supplementaryCount;

    @Column(name = "IS_PROCESSED")
    private Integer isProcessed;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "PROGRESS_FLAG")
    private Integer progressFlag;

    @Column(name = "REQUEST_TYPE_ID", length = 50)
    private String requestTypeId;

    @Column(name = "PRIMARY_PAN", length = 50)
    private String primaryPan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_TYPE_ID", insertable = false, updatable = false)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_PRODUCT_ID", insertable = false, updatable = false)
    private CardProduct cardProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID", insertable = false, updatable = false)
    private Branch branch;

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public Integer getSupplementaryCount() { return supplementaryCount; }
    public void setSupplementaryCount(Integer supplementaryCount) { this.supplementaryCount = supplementaryCount; }
    public Integer getIsProcessed() { return isProcessed; }
    public void setIsProcessed(Integer isProcessed) { this.isProcessed = isProcessed; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Integer getProgressFlag() { return progressFlag; }
    public void setProgressFlag(Integer progressFlag) { this.progressFlag = progressFlag; }
    public String getRequestTypeId() { return requestTypeId; }
    public void setRequestTypeId(String requestTypeId) { this.requestTypeId = requestTypeId; }
    public String getPrimaryPan() { return primaryPan; }
    public void setPrimaryPan(String primaryPan) { this.primaryPan = primaryPan; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public CardProduct getCardProduct() { return cardProduct; }
    public void setCardProduct(CardProduct cardProduct) { this.cardProduct = cardProduct; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public Long getCardProductId() { return cardProductId; }
    public void setCardProductId(Long cardProductId) { this.cardProductId = cardProductId; }
}
