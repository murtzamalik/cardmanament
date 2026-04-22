package com.cms.business.manager;

import com.cms.common.dto.AuthUserDto;
import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.common.service.ResponseHelperService;
import com.cms.dal.entity.UsmGroupPermission;
import com.cms.dal.entity.UsmUser;
import com.cms.dal.entity.UsmUserGroup;
import com.cms.dal.repository.UsmGroupPermissionRepository;
import com.cms.dal.repository.UsmUserGroupRepository;
import com.cms.dal.repository.UsmUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManager extends AbstractManagerStub {

    private final UsmUserRepository usmUserRepository;
    private final UsmUserGroupRepository usmUserGroupRepository;
    private final UsmGroupPermissionRepository usmGroupPermissionRepository;

    @Value("${app.default-app-id:CMS}")
    private String defaultAppId;

    public UserManager(ResponseHelperService responseHelper,
                       UsmUserRepository usmUserRepository,
                       UsmUserGroupRepository usmUserGroupRepository,
                       UsmGroupPermissionRepository usmGroupPermissionRepository) {
        super(responseHelper);
        this.usmUserRepository = usmUserRepository;
        this.usmUserGroupRepository = usmUserGroupRepository;
        this.usmGroupPermissionRepository = usmGroupPermissionRepository;
    }

    /**
     * Validates credentials and returns user + roles for JWT issuance. Used by AuthService.
     * Uses plain password comparison (Option A) per plan; BCrypt can be added later.
     */
    public Optional<AuthUserDto> authenticate(String loginId, String password, String appId) {
        if (loginId == null || loginId.isBlank()) return Optional.empty();
        String effectiveAppId = appId != null && !appId.isBlank() ? appId : defaultAppId;
        Optional<UsmUser> opt = usmUserRepository.findByLoginIdAndAppId(loginId, effectiveAppId);
        if (opt.isEmpty()) return Optional.empty();
        UsmUser user = opt.get();
        if (!isUserActive(user)) return Optional.empty();
        String storedPassword = user.getPassword();
        if (storedPassword == null || !storedPassword.equals(password)) return Optional.empty();
        List<String> roles = resolveRoles(loginId);
        return Optional.of(new AuthUserDto(user.getLoginId(), user.getFullName(), roles));
    }

    @Override
    protected boolean dispatch(String methodName, IProcessMessage requestMessage, IProcessMessage responseMessage) {
        return switch (methodName != null ? methodName : "") {
            case "Authenticate" -> authenticateBlr(requestMessage, responseMessage);
            case "GetAllUsers" -> getAllUsers(requestMessage, responseMessage);
            case "SearchUser" -> searchUser(requestMessage, responseMessage);
            default -> {
                responseHelper.setResponse(responseMessage, ResponseCodeEnum.BadRequest, "Method not implemented: " + methodName);
                yield false;
            }
        };
    }

    private boolean authenticateBlr(IProcessMessage req, IProcessMessage res) {
        String loginId = getStr(req, "LOGIN_ID");
        String password = getStr(req, "PASSWORD");
        String appId = getStr(req, "APP_ID");
        Optional<AuthUserDto> auth = authenticate(loginId, password, appId);
        if (auth.isEmpty()) {
            responseHelper.setResponse(res, ResponseCodeEnum.Unauthorized, "Invalid credentials");
            return true;
        }
        AuthUserDto dto = auth.get();
        res.setMsgObjArray(java.util.Map.of(
            "loginId", dto.getLoginId(),
            "fullName", dto.getFullName(),
            "roles", dto.getRoles()
        ));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    private boolean getAllUsers(IProcessMessage req, IProcessMessage res) {
        String appId = getStr(req, "APP_ID");
        String effectiveAppId = appId != null && !appId.isBlank() ? appId : defaultAppId;
        List<UsmUser> users = usmUserRepository.findAll().stream()
            .filter(u -> effectiveAppId.equals(u.getAppId()))
            .filter(this::isUserActive)
            .toList();
        res.setMsgObjArray(java.util.Map.of("list", users));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    private boolean searchUser(IProcessMessage req, IProcessMessage res) {
        String nameFilter = getStr(req, "FULL_NAME");
        String loginIdFilter = getStr(req, "LOGIN_ID");
        String appId = getStr(req, "APP_ID");
        String effectiveAppId = appId != null && !appId.isBlank() ? appId : defaultAppId;
        List<UsmUser> all = usmUserRepository.findAll().stream()
            .filter(u -> effectiveAppId.equals(u.getAppId()))
            .filter(this::isUserActive)
            .toList();
        List<UsmUser> filtered = all.stream()
            .filter(u -> {
                if (loginIdFilter != null && !loginIdFilter.isBlank()
                    && (u.getLoginId() == null || !u.getLoginId().toLowerCase().contains(loginIdFilter.toLowerCase())))
                    return false;
                if (nameFilter != null && !nameFilter.isBlank()
                    && (u.getFullName() == null || !u.getFullName().toLowerCase().contains(nameFilter.toLowerCase())))
                    return false;
                return true;
            })
            .toList();
        res.setMsgObjArray(java.util.Map.of("list", filtered));
        res.setSuccess(true);
        responseHelper.setResponse(res, ResponseCodeEnum.Success);
        return true;
    }

    private boolean isUserActive(UsmUser user) {
        if (user.getWhenDeleted() != null) return false;
        BigDecimal active = user.getIsActive();
        return active != null && active.intValue() == 1;
    }

    private List<String> resolveRoles(String loginId) {
        List<UsmUserGroup> userGroups = usmUserGroupRepository.findByLoginId(loginId);
        log.info("DEBUG resolveRoles: loginId={} userGroups={}", loginId, userGroups.size());
        if (userGroups == null || userGroups.isEmpty()) return List.of("USER");
        Set<String> groupIds = userGroups.stream()
            .map(UsmUserGroup::getGroupId)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toSet());
        log.info("DEBUG resolveRoles: groupIds={}", groupIds);
        if (groupIds.isEmpty()) return List.of("USER");
        List<String> permissions = new ArrayList<>();
        for (String groupId : groupIds) {
            List<UsmGroupPermission> perms = usmGroupPermissionRepository.findByGroupId(groupId);
            log.info("DEBUG resolveRoles: groupId={} perms={}", groupId, perms.size());
            if (perms != null) {
                perms.stream()
                    .map(UsmGroupPermission::getPermissionId)
                    .filter(java.util.Objects::nonNull)
                    .forEach(permissions::add);
            }
        }
        log.info("DEBUG resolveRoles: final permissions={}", permissions);
        return permissions.isEmpty() ? List.of("USER") : permissions;
    }

    private static String getStr(IProcessMessage msg, String key) {
        return msg.getMsgData() != null ? msg.getMsgData().get(key) : null;
    }
}
