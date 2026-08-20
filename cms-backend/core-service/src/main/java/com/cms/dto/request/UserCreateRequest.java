package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class UserCreateRequest {
    @NotBlank(message = "loginId is required")
    private String loginId;
    private String password;
    private String fullName;
    private String emailAddress;
    private String appId;
    private List<String> groupIds;

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public List<String> getGroupIds() { return groupIds; }
    public void setGroupIds(List<String> groupIds) { this.groupIds = groupIds; }
}
