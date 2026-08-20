package com.cms.common.model;

import com.cms.common.enums.MessageType;

/**
 * Wraps IProcessMessage with channel context for use inside Core.
 */
public class ProcMessage extends BizMessage {

    private int channelId;
    private MessageType messageType;

    public ProcMessage() {}

    public ProcMessage(int channelId, MessageType messageType, IProcessMessage bizMsg) {
        this.channelId = channelId;
        this.messageType = messageType;
        if (bizMsg != null) {
            setMsgData(bizMsg.getMsgData());
            setMsgObjData(bizMsg.getMsgObjData());
            setMsgObjArray(bizMsg.getMsgObjArray());
            setHeaders(bizMsg.getHeaders());
            setLoginId(bizMsg.getLoginId());
            setPermissionId(bizMsg.getPermissionId());
            setUserActionType(bizMsg.getUserActionType());
            setEntityId(bizMsg.getEntityId());
            setMachineName(bizMsg.getMachineName());
        }
    }

    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
}
