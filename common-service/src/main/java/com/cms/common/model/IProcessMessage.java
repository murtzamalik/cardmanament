package com.cms.common.model;

import com.cms.common.dto.CmsResponse;
import com.cms.common.enums.UserActionType;

import java.util.Dictionary;
import java.util.Map;

public interface IProcessMessage {

    Map<String, String> getMsgData();
    void setMsgData(Map<String, String> msgData);

    boolean isSuccess();
    void setSuccess(boolean success);

    String getMessage();
    void setMessage(String message);

    String getLoginId();
    void setLoginId(String loginId);

    String getPermissionId();
    void setPermissionId(String permissionId);

    UserActionType getUserActionType();
    void setUserActionType(UserActionType userActionType);

    CmsResponse getCmsResponse();
    void setCmsResponse(CmsResponse cmsResponse);

    Object getMsgObjData();
    void setMsgObjData(Object msgObjData);

    Map<String, Object> getMsgObjArray();
    void setMsgObjArray(Map<String, Object> msgObjArray);

    Map<String, String> getHeaders();
    void setHeaders(Map<String, String> headers);

    String getEntityId();
    void setEntityId(String entityId);

    String getMachineName();
    void setMachineName(String machineName);
}
