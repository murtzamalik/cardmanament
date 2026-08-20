package com.cms.common.bizprocess;

import com.cms.common.enums.MessageType;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * In-memory model of a business process with state table (loaded from BIZ_PROCESS / BIZ_PROCESS_STATES).
 */
public class BizProcessModel {

    private long channelId;
    private MessageType messageType;
    private String name;
    private String description;
    private int actionType;
    private final SortedMap<Integer, ProcessState> stateTable = new TreeMap<>();

    public long getChannelId() { return channelId; }
    public void setChannelId(long channelId) { this.channelId = channelId; }
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getActionType() { return actionType; }
    public void setActionType(int actionType) { this.actionType = actionType; }
    public SortedMap<Integer, ProcessState> getStateTable() { return stateTable; }
}
