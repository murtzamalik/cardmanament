package com.cms.dal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_ACCOUNT")
public class CardAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_account_seq_gen")
    @SequenceGenerator(name = "card_account_seq_gen", sequenceName = "CARD_ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "CA_ID")
    private Long caId;

    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name = "PAN", length = 50)
    private String pan;

    @Column(name = "ACCOUNT_NUM", length = 50)
    private String accountNum;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDateTime effectiveFrom;

    @Column(name = "RELATIONSHIP_NUM", length = 50)
    private String relationshipNum;

    @Column(name = "EFFECTIVE_TO")
    private LocalDateTime effectiveTo;

    @Column(name = "IS_OVERALL_DEFAULT")
    private Integer isOverallDefault;

    @Column(name = "IS_ACCT_TYPE_DEFAULT")
    private Integer isAcctTypeDefault;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID", insertable = false, updatable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)
    private Account account;

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public String getRelationshipNum() { return relationshipNum; }
    public void setRelationshipNum(String relationshipNum) { this.relationshipNum = relationshipNum; }
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    public Integer getIsOverallDefault() { return isOverallDefault; }
    public void setIsOverallDefault(Integer isOverallDefault) { this.isOverallDefault = isOverallDefault; }
    public Integer getIsAcctTypeDefault() { return isAcctTypeDefault; }
    public void setIsAcctTypeDefault(Integer isAcctTypeDefault) { this.isAcctTypeDefault = isAcctTypeDefault; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Long getCaId() { return caId; }
    public void setCaId(Long caId) { this.caId = caId; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}
