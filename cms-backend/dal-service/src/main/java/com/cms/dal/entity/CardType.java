package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_TYPE")
public class CardType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_type_seq_gen")
    @SequenceGenerator(name = "card_type_seq_gen", sequenceName = "CARD_TYPE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CARD_TYPE_CODE", length = 50)
    private String cardTypeCode;

    @Column(name = "IS_SUPP_TYPE")
    private Integer isSuppType;

    @Column(name = "SUPP_TYPE_CODE", length = 50)
    private String suppTypeCode;

    @Column(name = "CARD_TYPE_NAME", length = 255)
    private String cardTypeName;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "SUPPLEMENTARY_ALLOWED")
    private Long supplementaryAllowed;

    @Column(name = "PRODUCT_CODE", length = 50)
    private String productCode;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    /** Default limit profile applied to new cards of this type on approve/generate. */
    @Column(name = "DEFAULT_LIMIT_PROFILE_ID")
    private Long defaultLimitProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_LIMIT_PROFILE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private LimitProfile defaultLimitProfile;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "PAN_LENGTH")
    private Integer panLength;

    @Column(name = "BIN")
    private Integer bin;

    @Column(name = "EXP_PERIOD")
    private Integer expPeriod;

    @Column(name = "PAN_SEQUENCE_NAME", length = 100)
    private String panSequenceName;

    @Column(name = "PAN_SEQUENCE_LENGTH")
    private Integer panSequenceLength;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private CardProduct product;

    public String getCardTypeCode() { return cardTypeCode; }
    public void setCardTypeCode(String cardTypeCode) { this.cardTypeCode = cardTypeCode; }
    public Integer getIsSuppType() { return isSuppType; }
    public void setIsSuppType(Integer isSuppType) { this.isSuppType = isSuppType; }
    public String getSuppTypeCode() { return suppTypeCode; }
    public void setSuppTypeCode(String suppTypeCode) { this.suppTypeCode = suppTypeCode; }
    public String getCardTypeName() { return cardTypeName; }
    public void setCardTypeName(String cardTypeName) { this.cardTypeName = cardTypeName; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getSupplementaryAllowed() { return supplementaryAllowed; }
    public void setSupplementaryAllowed(Long supplementaryAllowed) { this.supplementaryAllowed = supplementaryAllowed; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public Integer getPanLength() { return panLength; }
    public void setPanLength(Integer panLength) { this.panLength = panLength; }
    public Integer getBin() { return bin; }
    public void setBin(Integer bin) { this.bin = bin; }
    public Integer getExpPeriod() { return expPeriod; }
    public void setExpPeriod(Integer expPeriod) { this.expPeriod = expPeriod; }
    public String getPanSequenceName() { return panSequenceName; }
    public void setPanSequenceName(String panSequenceName) { this.panSequenceName = panSequenceName; }
    public Integer getPanSequenceLength() { return panSequenceLength; }
    public void setPanSequenceLength(Integer panSequenceLength) { this.panSequenceLength = panSequenceLength; }
    public CardProduct getProduct() { return product; }
    public void setProduct(CardProduct product) { this.product = product; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getDefaultLimitProfileId() { return defaultLimitProfileId; }
    public void setDefaultLimitProfileId(Long defaultLimitProfileId) { this.defaultLimitProfileId = defaultLimitProfileId; }
    public LimitProfile getDefaultLimitProfile() { return defaultLimitProfile; }
    public void setDefaultLimitProfile(LimitProfile defaultLimitProfile) { this.defaultLimitProfile = defaultLimitProfile; }
}
