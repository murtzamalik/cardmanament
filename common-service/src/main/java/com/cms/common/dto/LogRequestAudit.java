package com.cms.common.dto;

import java.io.Serializable;

public class LogRequestAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    private String screenEventOrigin;
    private String userActionType;
    private String bizProcessMethodFullName;
    private String loginId;
    private String actionType;
    private String permissionName;
    private String loggerName;
    private String logType;
    private String machineName;

    public String getScreenEventOrigin() { return screenEventOrigin; }
    public void setScreenEventOrigin(String screenEventOrigin) { this.screenEventOrigin = screenEventOrigin; }
    public String getUserActionType() { return userActionType; }
    public void setUserActionType(String userActionType) { this.userActionType = userActionType; }
    public String getBizProcessMethodFullName() { return bizProcessMethodFullName; }
    public void setBizProcessMethodFullName(String bizProcessMethodFullName) { this.bizProcessMethodFullName = bizProcessMethodFullName; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public String getLoggerName() { return loggerName; }
    public void setLoggerName(String loggerName) { this.loggerName = loggerName; }
    public String getLogType() { return logType; }
    public void setLogType(String logType) { this.logType = logType; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
}
