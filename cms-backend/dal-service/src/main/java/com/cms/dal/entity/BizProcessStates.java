package com.cms.dal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BIZ_PROCESS_STATES")
public class BizProcessStates {

    @Id
    @Column(name = "PROCESSSTATEID")
    private Long processStateId;

    @Column(name = "CLASSNAME", length = 255)
    private String className;

    @Column(name = "METHODNAME", length = 255)
    private String methodName;

    @Column(name = "BIZPROCESSID")
    private Long bizProcessId;

    @Column(name = "NEXTSEQNUM_SUCCESS")
    private Integer nextSeqNumSuccess;

    @Column(name = "NEXTSEQNUM_FAIL")
    private Integer nextSeqNumFail;

    @Column(name = "SEQUENCENUMBER")
    private Integer sequenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIZPROCESSID", insertable = false, updatable = false)
    private BizProcess bizProcess;

    public Long getProcessStateId() { return processStateId; }
    public void setProcessStateId(Long processStateId) { this.processStateId = processStateId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public Long getBizProcessId() { return bizProcessId; }
    public void setBizProcessId(Long bizProcessId) { this.bizProcessId = bizProcessId; }
    public Integer getNextSeqNumSuccess() { return nextSeqNumSuccess; }
    public void setNextSeqNumSuccess(Integer nextSeqNumSuccess) { this.nextSeqNumSuccess = nextSeqNumSuccess; }
    public Integer getNextSeqNumFail() { return nextSeqNumFail; }
    public void setNextSeqNumFail(Integer nextSeqNumFail) { this.nextSeqNumFail = nextSeqNumFail; }
    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public BizProcess getBizProcess() { return bizProcess; }
    public void setBizProcess(BizProcess bizProcess) { this.bizProcess = bizProcess; }
}
