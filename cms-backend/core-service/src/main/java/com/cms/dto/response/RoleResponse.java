package com.cms.dto.response;


public class RoleResponse {
    private Long id;
    private String groupId;
    private String groupName;
    private Boolean active;
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
