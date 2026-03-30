package com.cms.common.bizprocess;

import java.io.Serializable;

public class ProcessState implements Serializable {

    private int sequenceNum;
    private String className;
    private String methodName;
    private Integer nextSeqNumSuccess;
    private Integer nextSeqNumFail;

    public int getSequenceNum() { return sequenceNum; }
    public void setSequenceNum(int sequenceNum) { this.sequenceNum = sequenceNum; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public Integer getNextSeqNum_Success() { return nextSeqNumSuccess; }
    public void setNextSeqNum_Success(Integer nextSeqNumSuccess) { this.nextSeqNumSuccess = nextSeqNumSuccess; }
    public Integer getNextSeqNum_Fail() { return nextSeqNumFail; }
    public void setNextSeqNum_Fail(Integer nextSeqNumFail) { this.nextSeqNumFail = nextSeqNumFail; }
}
