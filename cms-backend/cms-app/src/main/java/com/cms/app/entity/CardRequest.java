package com.cms.app.entity;

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
}
