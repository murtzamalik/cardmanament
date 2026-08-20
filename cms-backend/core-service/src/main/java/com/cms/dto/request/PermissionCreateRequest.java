package com.cms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PermissionCreateRequest {
    @NotBlank(message = "permissionId is required")
    private String permissionId;
    private String perParentId;
    private String permissionName;
    private String permissionType;
    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public String getPerParentId() { return perParentId; }
    public void setPerParentId(String perParentId) { this.perParentId = perParentId; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
}
