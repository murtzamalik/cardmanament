package com.cms.dal.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BIZ_PROCESS")
public class BizProcess {

    @Id
    @Column(name = "BIZPROCESSID")
    private Long bizProcessId;

    @Column(name = "BIZPROCESSNAME", length = 255)
    private String bizProcessName;

    @Column(name = "CHANNELID")
    private Long channelId;

    @Column(name = "MESSAGETYPE")
    private Integer messageType;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "ACTION_TYPE")
    private Integer actionType;

    @OneToMany(mappedBy = "bizProcess", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber")
    private List<BizProcessStates> bizProcessStates = new ArrayList<>();

    public Long getBizProcessId() { return bizProcessId; }
    public void setBizProcessId(Long bizProcessId) { this.bizProcessId = bizProcessId; }
    public String getBizProcessName() { return bizProcessName; }
    public void setBizProcessName(String bizProcessName) { this.bizProcessName = bizProcessName; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Integer getMessageType() { return messageType; }
    public void setMessageType(Integer messageType) { this.messageType = messageType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getActionType() { return actionType; }
    public void setActionType(Integer actionType) { this.actionType = actionType; }
    public List<BizProcessStates> getBizProcessStates() { return bizProcessStates; }
    public void setBizProcessStates(List<BizProcessStates> bizProcessStates) { this.bizProcessStates = bizProcessStates; }
}
