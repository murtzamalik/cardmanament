package com.cms.app.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String loginId;
    private String fullName;
    private String token;
    private List<String> groupIds;
}
