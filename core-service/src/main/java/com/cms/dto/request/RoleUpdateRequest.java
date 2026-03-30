package com.cms.dto.request;

public class RoleUpdateRequest {
    private String groupName;
    private Boolean active;

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
