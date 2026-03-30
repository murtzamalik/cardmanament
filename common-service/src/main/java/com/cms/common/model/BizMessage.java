package com.cms.common.model;

import com.cms.common.dto.CmsResponse;
import com.cms.common.enums.UserActionType;

import java.util.HashMap;
import java.util.Map;

public class BizMessage implements IProcessMessage {

    private Map<String, String> msgData = new HashMap<>();
    private boolean success;
    private String message;
    private String loginId;
    private String permissionId;
    private UserActionType userActionType = UserActionType.Undefined;
    private CmsResponse cmsResponse;
    private Object msgObjData;
    private Map<String, Object> msgObjArray = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String entityId;
    private String machineName;

    public BizMessage() {}

    public BizMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public Map<String, String> getMsgData() { return msgData; }
    @Override
    public void setMsgData(Map<String, String> msgData) { this.msgData = msgData != null ? msgData : new HashMap<>(); }

    @Override
    public boolean isSuccess() { return success; }
    @Override
    public void setSuccess(boolean success) { this.success = success; }

    @Override
    public String getMessage() { return message; }
    @Override
    public void setMessage(String message) { this.message = message; }

    @Override
    public String getLoginId() { return loginId; }
    @Override
    public void setLoginId(String loginId) { this.loginId = loginId; }

    @Override
    public String getPermissionId() { return permissionId; }
    @Override
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }

    @Override
    public UserActionType getUserActionType() { return userActionType; }
    @Override
    public void setUserActionType(UserActionType userActionType) { this.userActionType = userActionType; }

    @Override
    public CmsResponse getCmsResponse() { return cmsResponse; }
    @Override
    public void setCmsResponse(CmsResponse cmsResponse) { this.cmsResponse = cmsResponse; }

    @Override
    public Object getMsgObjData() { return msgObjData; }
    @Override
    public void setMsgObjData(Object msgObjData) { this.msgObjData = msgObjData; }

    @Override
    public Map<String, Object> getMsgObjArray() { return msgObjArray; }
    @Override
    public void setMsgObjArray(Map<String, Object> msgObjArray) { this.msgObjArray = msgObjArray != null ? msgObjArray : new HashMap<>(); }

    @Override
    public Map<String, String> getHeaders() { return headers; }
    @Override
    public void setHeaders(Map<String, String> headers) { this.headers = headers != null ? headers : new HashMap<>(); }

    @Override
    public String getEntityId() { return entityId; }
    @Override
    public void setEntityId(String entityId) { this.entityId = entityId; }

    @Override
    public String getMachineName() { return machineName; }
    @Override
    public void setMachineName(String machineName) { this.machineName = machineName; }
}
