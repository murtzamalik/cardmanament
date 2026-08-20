package com.cms.controller;

import com.cms.dto.response.ApiResponse;
import com.cms.dto.response.MenuResponse;
import com.cms.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/my-menus")
@Tag(name = "My Menus", description = "Current user menu API")
public class MyMenuController {

    private final MenuService menuService;

    public MyMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(summary = "Get menus for current user")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMyMenus(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .toList();
        return ResponseEntity.ok(ApiResponse.ok(menuService.getMenusForRoles(roles)));
    }
}
