package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CardLimitCustomizedCreateRequest {

    @NotBlank(message = "pan is required")
    private String pan;

    @NotBlank(message = "tranCode is required")
    private String tranCode;

    @NotNull(message = "customizedLimit is required")
    private BigDecimal customizedLimit;

    private Integer customizedTranCount;
    private LocalDateTime cycleBeginDate;
    private Boolean isActive;
    private String createdBy;

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getTranCode() { return tranCode; }
    public void setTranCode(String tranCode) { this.tranCode = tranCode; }
    public BigDecimal getCustomizedLimit() { return customizedLimit; }
    public void setCustomizedLimit(BigDecimal customizedLimit) { this.customizedLimit = customizedLimit; }
    public Integer getCustomizedTranCount() { return customizedTranCount; }
    public void setCustomizedTranCount(Integer customizedTranCount) { this.customizedTranCount = customizedTranCount; }
    public LocalDateTime getCycleBeginDate() { return cycleBeginDate; }
    public void setCycleBeginDate(LocalDateTime cycleBeginDate) { this.cycleBeginDate = cycleBeginDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
