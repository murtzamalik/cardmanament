package com.cms.dal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_LIMIT_CUSTOMIZED")
public class CardLimitCustomized {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_limit_customized_seq_gen")
    @SequenceGenerator(name = "card_limit_customized_seq_gen", sequenceName = "CARD_LIMIT_CUSTOMIZED_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PAN", length = 50, nullable = false)
    private String pan;

    @Column(name = "TRAN_CODE", length = 20, nullable = false)
    private String tranCode;

    @Column(name = "CUSTOMIZED_LIMIT", precision = 18, scale = 2, nullable = false)
    private BigDecimal customizedLimit;

    @Column(name = "CUSTOMIZED_TRAN_COUNT")
    private Integer customizedTranCount;

    @Column(name = "CYCLE_BEGIN_DATE")
    private LocalDateTime cycleBeginDate;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY", length = 50)
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
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
