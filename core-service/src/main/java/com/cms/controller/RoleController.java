package com.cms.controller;

import com.cms.dto.request.RoleCreateRequest;
import com.cms.dto.request.RoleMenuAssignRequest;
import com.cms.dto.request.RoleUpdateRequest;
import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.MenuResponse;
import com.cms.dto.response.RoleResponse;
import com.cms.service.MenuService;
import com.cms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Role (group) management API")
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    public RoleController(RoleService roleService, MenuService menuService) {
        this.roleService = roleService;
        this.menuService = menuService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Create role")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Role created successfully", roleService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(roleService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get role by groupId")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(roleService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Update role")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Role updated successfully", roleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Delete role")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Role deleted", null));
    }

    @GetMapping("/{roleCode}/menus")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get menus assigned to role")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getRoleMenus(@PathVariable String roleCode) {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getMenusForRoleCode(roleCode)));
    }

    @PostMapping("/{roleCode}/menus")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Assign menus to role (replace all)")
    public ResponseEntity<ApiResponse<Void>> assignRoleMenus(@PathVariable String roleCode, @RequestBody RoleMenuAssignRequest request) {
        menuService.replaceRoleMenus(roleCode, request != null ? request.getMenuIds() : null);
        return ResponseEntity.ok(ApiResponse.ok("Role menus updated", null));
    }

    @DeleteMapping("/{roleCode}/menus/{menuId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Remove menu from role")
    public ResponseEntity<ApiResponse<Void>> removeRoleMenu(@PathVariable String roleCode, @PathVariable Long menuId) {
        menuService.removeRoleMenu(roleCode, menuId);
        return ResponseEntity.ok(ApiResponse.ok("Role menu removed", null));
    }
}
