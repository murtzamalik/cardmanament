package com.cms.controller;

import com.cms.dto.response.ApiResponse;
import com.cms.dto.request.MenuCreateRequest;
import com.cms.dto.request.MenuUpdateRequest;
import com.cms.dto.response.MenuResponse;
import com.cms.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@Tag(name = "Menus", description = "Menu access API")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/my-menus")
    @Operation(summary = "Get menus for current user based on their roles")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMyMenus(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(menuService.getMenusForRoles(roles)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Get all menus")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenus() {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getAllMenus()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Create menu")
    public ResponseEntity<ApiResponse<MenuResponse>> create(@Valid @RequestBody MenuCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Menu created successfully", menuService.createMenu(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Update menu")
    public ResponseEntity<ApiResponse<MenuResponse>> update(@PathVariable Long id, @RequestBody MenuUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Menu updated successfully", menuService.updateMenu(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_MANAGER','SUPER_ADMIN')")
    @Operation(summary = "Delete menu")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.ok("Menu deleted", null));
    }
}
