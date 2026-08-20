package com.cms.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsmUserGroupId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "GROUP_ID", length = 50)
    private String groupId;

    @Column(name = "LOGIN_ID", length = 50)
    private String loginId;

    public UsmUserGroupId() {}

    public UsmUserGroupId(String groupId, String loginId) {
        this.groupId = groupId;
        this.loginId = loginId;
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsmUserGroupId that = (UsmUserGroupId) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(loginId, that.loginId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, loginId);
    }
}
