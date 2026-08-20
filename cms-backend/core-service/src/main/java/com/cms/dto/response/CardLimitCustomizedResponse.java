package com.cms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CardLimitCustomizedResponse {

    private Long id;
    private String pan;
    private String tranCode;
    private BigDecimal customizedLimit;
    private Integer customizedTranCount;
    private LocalDateTime cycleBeginDate;
    private Boolean isActive;
    private LocalDateTime createdOn;
    private String createdBy;
    private LocalDateTime updatedOn;
    private String updatedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
