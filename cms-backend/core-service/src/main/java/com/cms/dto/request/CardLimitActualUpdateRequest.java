package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CardLimitActualUpdateRequest {

    @NotBlank(message = "pan is required")
    private String pan;

    @NotBlank(message = "channelCode is required")
    private String channelCode;

    @NotBlank(message = "tranCode is required")
    private String tranCode;

    @NotNull(message = "availableLimit is required")
    private BigDecimal availableLimit;

    private Long availableTranCount;
    private LocalDate cycleBeginDate;
    private String updatedBy;

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getTranCode() { return tranCode; }
    public void setTranCode(String tranCode) { this.tranCode = tranCode; }
    public BigDecimal getAvailableLimit() { return availableLimit; }
    public void setAvailableLimit(BigDecimal availableLimit) { this.availableLimit = availableLimit; }
    public Long getAvailableTranCount() { return availableTranCount; }
    public void setAvailableTranCount(Long availableTranCount) { this.availableTranCount = availableTranCount; }
    public LocalDate getCycleBeginDate() { return cycleBeginDate; }
    public void setCycleBeginDate(LocalDate cycleBeginDate) { this.cycleBeginDate = cycleBeginDate; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
