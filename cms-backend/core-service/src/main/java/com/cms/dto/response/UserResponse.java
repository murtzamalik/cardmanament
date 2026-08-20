package com.cms.dto.response;

import java.util.List;

public class UserResponse {
    private Long id;
    private String loginId;
    private String fullName;
    private String emailAddress;
    private String appId;
    private Boolean active;
    private List<String> roleIds;
    private List<String> roleNames;

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public List<String> getRoleIds() { return roleIds; }
    public void setRoleIds(List<String> roleIds) { this.roleIds = roleIds; }
    public List<String> getRoleNames() { return roleNames; }
    public void setRoleNames(List<String> roleNames) { this.roleNames = roleNames; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
