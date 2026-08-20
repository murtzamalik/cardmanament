package com.cms.controller;

import com.cms.dto.request.UserCreateRequest;
import com.cms.dto.request.UserRoleAssignRequest;
import com.cms.dto.request.UserUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.UserResponse;
import com.cms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Create user")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(
            request.getLoginId(),
            request.getPassword() != null ? request.getPassword() : "",
            request.getFullName(),
            request.getAppId(),
            request.getGroupIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("User created successfully", userService.getUserResponse(request.getLoginId())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(userService.findAllResponses()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get user by id")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully", userService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Delete user (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get roles assigned to user")
    public ResponseEntity<ApiResponse<List<String>>> getUserRoles(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getRoleIdsByUserId(id)));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Assign role to user")
    public ResponseEntity<ApiResponse<Void>> assignUserRole(@PathVariable Long id, @Valid @RequestBody UserRoleAssignRequest request) {
        userService.assignRoleToUser(id, request.getGroupId());
        return ResponseEntity.ok(ApiResponse.ok("Role assigned to user", null));
    }

    @DeleteMapping("/{id}/roles/{roleCode}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Remove role from user")
    public ResponseEntity<ApiResponse<Void>> removeUserRole(@PathVariable Long id, @PathVariable String roleCode) {
        userService.removeRoleFromUser(id, roleCode);
        return ResponseEntity.ok(ApiResponse.ok("Role removed from user", null));
    }
}
