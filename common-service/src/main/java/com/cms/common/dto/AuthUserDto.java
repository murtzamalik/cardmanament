package com.cms.common.dto;

import java.util.List;

/**
 * Result of successful authentication for JWT issuance.
 */
public class AuthUserDto {

    private String loginId;
    private String fullName;
    private List<String> roles;

    public AuthUserDto() {}

    public AuthUserDto(String loginId, String fullName, List<String> roles) {
        this.loginId = loginId;
        this.fullName = fullName;
        this.roles = roles != null ? List.copyOf(roles) : List.of();
    }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles != null ? List.copyOf(roles) : List.of(); }
}
