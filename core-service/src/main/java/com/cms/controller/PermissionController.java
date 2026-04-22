package com.cms.controller;

import com.cms.dto.request.PermissionCreateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.PermissionResponse;
import com.cms.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Permission management API")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Create permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@Valid @RequestBody PermissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Permission created successfully", permissionService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get all permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(permissionService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get permission by id")
    public ResponseEntity<ApiResponse<PermissionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(permissionService.getById(id)));
    }
}
