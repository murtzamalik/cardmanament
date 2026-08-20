package com.cms.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CardLimitCustomizedUpdateRequest {

    private BigDecimal customizedLimit;
    private Integer customizedTranCount;
    private LocalDateTime cycleBeginDate;
    private Boolean isActive;
    private String updatedBy;

    public BigDecimal getCustomizedLimit() { return customizedLimit; }
    public void setCustomizedLimit(BigDecimal customizedLimit) { this.customizedLimit = customizedLimit; }
    public Integer getCustomizedTranCount() { return customizedTranCount; }
    public void setCustomizedTranCount(Integer customizedTranCount) { this.customizedTranCount = customizedTranCount; }
    public LocalDateTime getCycleBeginDate() { return cycleBeginDate; }
    public void setCycleBeginDate(LocalDateTime cycleBeginDate) { this.cycleBeginDate = cycleBeginDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
