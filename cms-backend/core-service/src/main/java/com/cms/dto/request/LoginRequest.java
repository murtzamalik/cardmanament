package com.cms.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "loginId or username is required")
    private String loginId;

    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        if (loginId != null && !loginId.isBlank()) {
            this.loginId = loginId;
        }
    }

    /** Accept "username" from JSON as alias for loginId (e.g. frontend sends username) */
    @JsonProperty("username")
    public void setUsername(String username) {
        if (username != null && (this.loginId == null || this.loginId.isBlank())) {
            this.loginId = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
