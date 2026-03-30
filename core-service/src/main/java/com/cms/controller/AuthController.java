package com.cms.controller;

import com.cms.core.service.AuthService;
import com.cms.dto.request.LoginRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.LoginResponse;
import com.cms.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with loginId and password, returns JWT and user info")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(
            request.getLoginId(),
            request.getPassword() != null ? request.getPassword() : ""
        )
            .map(res -> ResponseEntity.ok(ApiResponse.ok("Login successful", res)))
            .orElseThrow(UnauthorizedException::new);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Issue a new JWT using current valid token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody(required = false) Map<String, String> body) {
        String token = body != null ? body.get("token") : null;
        if (token == null || token.isBlank())
            throw new UnauthorizedException("Token required");
        return authService.refresh(token)
            .map(res -> ResponseEntity.ok(ApiResponse.ok("Token refreshed", res)))
            .orElseThrow(UnauthorizedException::new);
    }
}
