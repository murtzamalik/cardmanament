package com.cms.dto.response;

import java.util.List;

public class LoginResponse {

    private String token;
    private String loginId;
    private String fullName;
    private Long expiresIn;
    private List<String> roles;
    private List<MenuResponse> menus;

    public List<MenuResponse> getMenus() { return menus; }
    public void setMenus(List<MenuResponse> menus) { this.menus = menus; }

    public LoginResponse() {
    }

    public LoginResponse(String token, String loginId, String fullName, Long expiresIn, List<String> roles) {
        this.token = token;
        this.loginId = loginId;
        this.fullName = fullName;
        this.expiresIn = expiresIn;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
