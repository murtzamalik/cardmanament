package com.cms.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CARD_LIMIT_ACTUAL")
@IdClass(CardLimitActualId.class)
public class CardLimitActual {

    @Id
    @Column(name = "PAN", length = 30, nullable = false)
    private String pan;

    @Id
    @Column(name = "CHANNEL_CODE", length = 30, nullable = false)
    private String channelCode;

    @Id
    @Column(name = "TRAN_CODE", length = 30, nullable = false)
    private String tranCode;

    @Column(name = "CYCLE_BEGIN_DATE", nullable = false)
    private LocalDate cycleBeginDate;

    @Column(name = "AVAILABLE_LIMIT", precision = 17, scale = 3, nullable = false)
    private BigDecimal availableLimit;

    @Column(name = "AVAILABLE_TRAN_COUNT", nullable = false)
    private Long availableTranCount;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDate createdOn;

    @Column(name = "CREATED_BY", length = 30, nullable = false)
    private String createdBy;

    @Column(name = "UPDATED_ON", nullable = false)
    private LocalDate updatedOn;

    @Column(name = "UPDATED_BY", length = 30, nullable = false)
    private String updatedBy;

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getTranCode() { return tranCode; }
    public void setTranCode(String tranCode) { this.tranCode = tranCode; }
    public LocalDate getCycleBeginDate() { return cycleBeginDate; }
    public void setCycleBeginDate(LocalDate cycleBeginDate) { this.cycleBeginDate = cycleBeginDate; }
    public BigDecimal getAvailableLimit() { return availableLimit; }
    public void setAvailableLimit(BigDecimal availableLimit) { this.availableLimit = availableLimit; }
    public Long getAvailableTranCount() { return availableTranCount; }
    public void setAvailableTranCount(Long availableTranCount) { this.availableTranCount = availableTranCount; }
    public LocalDate getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDate createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDate getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDate updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
