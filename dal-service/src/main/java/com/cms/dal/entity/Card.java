package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARD")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq_gen")
    @SequenceGenerator(name = "card_seq_gen", sequenceName = "CARD_SEQ", allocationSize = 1)
    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name = "PAN", length = 50)
    private String pan;

    @Column(name = "PAN_ENCRYPTED", length = 500)
    private String panEncrypted;

    @Column(name = "PAN_LAST4", length = 4)
    private String panLast4;

    @Column(name = "PAN_HASH", length = 128)
    private String panHash;

    @Column(name = "RELATIONSHIP_NUM", length = 50)
    private String relationshipNum;

    @Column(name = "CARD_TITLE", length = 255)
    private String cardTitle;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "CARD_TYPE_CODE", length = 50)
    private String cardTypeCode;

    @Column(name = "LIMIT_PROFILE", length = 50)
    private String limitProfile;

    @Column(name = "SUPPLEMENTARY_COUNT")
    private Integer supplementaryCount;

    @Column(name = "CARD_STATUS_CODE", length = 50)
    private String cardStatusCode;

    @Column(name = "PRIMARY_CARD_NUM", length = 50)
    private String primaryCardNum;

    @Column(name = "ACTIVATION_DATE")
    private LocalDateTime activationDate;

    @Column(name = "PRODUCT_CODE", length = 50)
    private String productCode;

    @Column(name = "ISSUED_DATE")
    private LocalDateTime issuedDate;

    @Column(name = "BRANCH_CODE", length = 50)
    private String branchCode;

    @Column(name = "BRANCH_ID")
    private Long branchId;

    @Column(name = "CARD_TYPE_ID")
    private Long cardTypeId;

    @Column(name = "CARD_PRODUCT_ID")
    private Long cardProductId;

    @Column(name = "CARD_STATUS_ID")
    private Long cardStatusId;

    @Column(name = "CARD_PRODUCTION_STATUS_REF_ID")
    private Long cardProductionStatusRefId;

    @Column(name = "LIMIT_PROFILE_ID")
    private Long limitProfileId;

    @Column(name = "CARD_PROD_STATUS_ID", length = 50)
    private String cardProdStatusId;

    @Column(name = "WHEN_DELETED")
    private LocalDateTime whenDeleted;

    @Column(name = "IS_REPLACED")
    private Integer isReplaced;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "REQUEST_TYPE", length = 50)
    private String requestType;

    @Column(name = "TRIM_PAN", length = 50)
    private String trimPan;

    @Column(name = "IS_MAKER")
    private Integer isMaker;

    @Column(name = "TO_CARD_STATUS_CODE", length = 50)
    private String toCardStatusCode;

    @Column(name = "ECOMMERCE_ALLOWED", length = 10)
    private String ecommerceAllowed;

    @Column(name = "CVV", length = 20)
    private String cvv;

    @Column(name = "CVV2", length = 20)
    private String cvv2;

    @Column(name = "ICVV", length = 20)
    private String icvv;

    @Column(name = "TRACK1_DATA", length = 500)
    private String track1Data;

    @Column(name = "TRACK2_DATA", length = 500)
    private String track2Data;

    @Column(name = "TO_CARD_TITLE", length = 255)
    private String toCardTitle;

    @Column(name = "TO_CARD_TYPE_CODE", length = 50)
    private String toCardTypeCode;

    @Column(name = "TO_DEBIT_PROD_CODE", length = 50)
    private String toDebitProdCode;

    @Column(name = "OPERATION_TYPE", length = 50)
    private String operationType;

    @Column(name = "EXPORT_FILE_PATH", length = 1024)
    private String exportFilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID", insertable = false, updatable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_STATUS_ID", insertable = false, updatable = false)
    private CardStatus cardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_PRODUCTION_STATUS_REF_ID", insertable = false, updatable = false)
    private CardProductionStatus cardProductionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_TYPE_ID", insertable = false, updatable = false)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_PRODUCT_ID", insertable = false, updatable = false)
    private CardProduct cardProduct;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardAccount> cardAccounts = new ArrayList<>();

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getPanEncrypted() { return panEncrypted; }
    public void setPanEncrypted(String panEncrypted) { this.panEncrypted = panEncrypted; }
    public String getPanLast4() { return panLast4; }
    public void setPanLast4(String panLast4) { this.panLast4 = panLast4; }
    public String getPanHash() { return panHash; }
    public void setPanHash(String panHash) { this.panHash = panHash; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public String getCardTitle() { return cardTitle; }
    public void setCardTitle(String cardTitle) { this.cardTitle = cardTitle; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public String getLimitProfile() { return limitProfile; }
    public void setLimitProfile(String limitProfile) { this.limitProfile = limitProfile; }
    public Integer getSupplementaryCount() { return supplementaryCount; }
    public void setSupplementaryCount(Integer supplementaryCount) { this.supplementaryCount = supplementaryCount; }
    public String getCardStatusCode() { return cardStatusCode; }
    public void setCardStatusCode(String cardStatusCode) { this.cardStatusCode = cardStatusCode; }
    public String getPrimaryCardNum() { return primaryCardNum; }
    public void setPrimaryCardNum(String primaryCardNum) { this.primaryCardNum = primaryCardNum; }
    public LocalDateTime getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDateTime activationDate) { this.activationDate = activationDate; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getCardProdStatusId() { return cardProdStatusId; }
    public void setCardProdStatusId(String cardProdStatusId) { this.cardProdStatusId = cardProdStatusId; }
    public LocalDateTime getWhenDeleted() { return whenDeleted; }
    public void setWhenDeleted(LocalDateTime whenDeleted) { this.whenDeleted = whenDeleted; }
    public Integer getIsReplaced() { return isReplaced; }
    public void setIsReplaced(Integer isReplaced) { this.isReplaced = isReplaced; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getTrimPan() { return trimPan; }
    public void setTrimPan(String trimPan) { this.trimPan = trimPan; }
    public Integer getIsMaker() { return isMaker; }
    public void setIsMaker(Integer isMaker) { this.isMaker = isMaker; }
    public String getToCardStatusCode() { return toCardStatusCode; }
    public void setToCardStatusCode(String toCardStatusCode) { this.toCardStatusCode = toCardStatusCode; }
    public String getEcommerceAllowed() { return ecommerceAllowed; }
    public void setEcommerceAllowed(String ecommerceAllowed) { this.ecommerceAllowed = ecommerceAllowed; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getCvv2() { return cvv2; }
    public void setCvv2(String cvv2) { this.cvv2 = cvv2; }
    public String getIcvv() { return icvv; }
    public void setIcvv(String icvv) { this.icvv = icvv; }
    public String getTrack1Data() { return track1Data; }
    public void setTrack1Data(String track1Data) { this.track1Data = track1Data; }
    public String getTrack2Data() { return track2Data; }
    public void setTrack2Data(String track2Data) { this.track2Data = track2Data; }
    public String getToCardTitle() { return toCardTitle; }
    public void setToCardTitle(String toCardTitle) { this.toCardTitle = toCardTitle; }
    public String getToCardTypeCode() { return toCardTypeCode; }
    public void setToCardTypeCode(String toCardTypeCode) { this.toCardTypeCode = toCardTypeCode; }
    public String getToDebitProdCode() { return toDebitProdCode; }
    public void setToDebitProdCode(String toDebitProdCode) { this.toDebitProdCode = toDebitProdCode; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getExportFilePath() { return exportFilePath; }
    public void setExportFilePath(String exportFilePath) { this.exportFilePath = exportFilePath; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public CardStatus getCardStatus() { return cardStatus; }
    public void setCardStatus(CardStatus cardStatus) { this.cardStatus = cardStatus; }
    public CardProductionStatus getCardProductionStatus() { return cardProductionStatus; }
    public void setCardProductionStatus(CardProductionStatus cardProductionStatus) { this.cardProductionStatus = cardProductionStatus; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public CardProduct getCardProduct() { return cardProduct; }
    public void setCardProduct(CardProduct cardProduct) { this.cardProduct = cardProduct; }
    public List<CardAccount> getCardAccounts() { return cardAccounts; }
    public void setCardAccounts(List<CardAccount> cardAccounts) { this.cardAccounts = cardAccounts != null ? cardAccounts : new ArrayList<>(); }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public Long getCardProductId() { return cardProductId; }
    public void setCardProductId(Long cardProductId) { this.cardProductId = cardProductId; }
    public Long getCardStatusId() { return cardStatusId; }
    public void setCardStatusId(Long cardStatusId) { this.cardStatusId = cardStatusId; }
    public Long getCardProductionStatusRefId() { return cardProductionStatusRefId; }
    public void setCardProductionStatusRefId(Long cardProductionStatusRefId) { this.cardProductionStatusRefId = cardProductionStatusRefId; }
    public Long getLimitProfileId() { return limitProfileId; }
    public void setLimitProfileId(Long limitProfileId) { this.limitProfileId = limitProfileId; }
}
