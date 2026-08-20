package com.cms.app.controller;

import com.cms.app.config.ResponseCode;
import com.cms.app.request.AuthRequest;
import com.cms.app.response.LoginResponse;
import com.cms.app.response.ResponseWrapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    public @ResponseBody ResponseWrapper<LoginResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        LoginResponse body = new LoginResponse();
        body.setLoginId(authRequest.getUsername());
        body.setFullName("CMS App User");
        body.setGroupIds(List.of("app"));
        body.setToken("cms-app-dev-token");

        ResponseWrapper<LoginResponse> response = new ResponseWrapper<>();
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(ResponseCode.getMessage(ResponseCode.SUCCESS));
        response.setResponseBody(body);
        return response;
    }
}
