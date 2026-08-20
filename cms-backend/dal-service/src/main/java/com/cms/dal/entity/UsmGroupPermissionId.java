package com.cms.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsmGroupPermissionId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "PERMISSION_ID", length = 50)
    private String permissionId;

    public UsmGroupPermissionId() {}

    public UsmGroupPermissionId(String groupId, String permissionId) {
        this.groupId = groupId;
        this.permissionId = permissionId;
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsmGroupPermissionId that = (UsmGroupPermissionId) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, permissionId);
    }
}
