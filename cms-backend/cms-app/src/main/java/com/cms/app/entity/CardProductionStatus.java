package com.cms.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_PRODUCTION_STATUS")
public class CardProductionStatus {

    @Id
    @Column(name = "CARD_PROD_STATUS_ID", length = 50)
    private String cardProdStatusId;

    @Column(name = "CARD_PROD_STATUS_NAME", length = 255)
    private String cardProdStatusName;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    public String getCardProdStatusId() { return cardProdStatusId; }
    public void setCardProdStatusId(String cardProdStatusId) { this.cardProdStatusId = cardProdStatusId; }
    public String getCardProdStatusName() { return cardProdStatusName; }
    public void setCardProdStatusName(String cardProdStatusName) { this.cardProdStatusName = cardProdStatusName; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
