package com.cms.common.bizprocess;

import com.cms.common.enums.MessageType;

import java.util.Objects;

public class BizProcessKey {

    private long channelId;
    private MessageType messageType;

    public BizProcessKey() {}

    public BizProcessKey(long channelId, MessageType messageType) {
        this.channelId = channelId;
        this.messageType = messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BizProcessKey that = (BizProcessKey) o;
        return channelId == that.channelId && messageType == that.messageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, messageType);
    }

    public long getChannelId() { return channelId; }
    public void setChannelId(long channelId) { this.channelId = channelId; }
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
}
