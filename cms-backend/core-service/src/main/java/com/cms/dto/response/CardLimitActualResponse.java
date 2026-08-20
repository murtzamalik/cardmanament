package com.cms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardLimitActualResponse {

    private String pan;
    private String channelCode;
    private String tranCode;
    private LocalDate cycleBeginDate;
    private BigDecimal availableLimit;
    private Long availableTranCount;
    private LocalDate createdOn;
    private String createdBy;
    private LocalDate updatedOn;
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
