package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserRoleAssignRequest {
    @NotBlank(message = "groupId is required")
    private String groupId;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}
