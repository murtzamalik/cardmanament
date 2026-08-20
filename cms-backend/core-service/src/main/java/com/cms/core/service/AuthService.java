package com.cms.core.service;

import com.cms.common.dto.AuthUserDto;
import com.cms.common.security.JwtService;
import com.cms.dto.response.LoginResponse;
import com.cms.dto.response.MenuResponse;
import com.cms.service.MenuService;
import com.cms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final MenuService menuService;

    @Value("${jwt.expiration-ms:900000}")
    private long expirationMs;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, JwtService jwtService, MenuService menuService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.menuService = menuService;
    }

    public Optional<LoginResponse> login(String loginId, String password) {
        Optional<AuthUserDto> auth = userService.authenticate(loginId, password);
        if (auth.isEmpty()) {
            log.debug("Login failed for loginId={}", loginId != null ? loginId : "(null)");
            return Optional.empty();
        }
        AuthUserDto user = auth.get();
        String token = jwtService.generateToken(user.getLoginId(), user.getRoles());
        long expiresInSeconds = expirationMs / 1000;
        // Fetch menus for user's roles
        java.util.List<MenuResponse> menus = menuService.getMenusForRoles(user.getRoles());
        LoginResponse r = new LoginResponse();
        r.setToken(token);
        r.setLoginId(user.getLoginId());
        r.setFullName(user.getFullName());
        r.setExpiresIn(expiresInSeconds);
        r.setRoles(user.getRoles());
        r.setMenus(menus);
        return Optional.of(r);
    }

    public Optional<LoginResponse> refresh(String token) {
        if (token == null || !jwtService.validateToken(token)) return Optional.empty();
        String loginId = jwtService.getLoginIdFromToken(token);
        java.util.List<String> roles = jwtService.getRolesFromToken(token);
        String newToken = jwtService.generateToken(loginId, roles);
        long expiresInSeconds = expirationMs / 1000;
        com.cms.dal.entity.UsmUser user = userService.getByLoginId(loginId);
        LoginResponse r = new LoginResponse();
        r.setToken(newToken);
        r.setLoginId(loginId);
        r.setFullName(user.getFullName());
        r.setExpiresIn(expiresInSeconds);
        r.setRoles(roles);
        r.setMenus(menuService.getMenusForRoles(roles));
        return Optional.of(r);
    }

    public void changePassword(String loginId, String currentPassword, String newPassword) {
        userService.changePassword(loginId, currentPassword, newPassword);
    }
}
