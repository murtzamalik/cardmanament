package com.cms.service.impl;

import com.cms.common.auth.PasswordRules;
import com.cms.common.dto.AuthUserDto;
import com.cms.dal.entity.UsmUser;
import com.cms.dal.entity.UsmUserGroup;
import com.cms.dal.repository.UsmGroupRepository;
import com.cms.dal.repository.UsmUserGroupRepository;
import com.cms.dal.repository.UsmUserRepository;
import com.cms.dto.request.UserUpdateRequest;
import com.cms.dto.response.UserResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements com.cms.service.UserService {

    private final UsmUserRepository usmUserRepository;
    private final UsmUserGroupRepository usmUserGroupRepository;
    private final UsmGroupRepository usmGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${app.default-app-id:CMS}")
    private String defaultAppId;

    public UserServiceImpl(UsmUserRepository usmUserRepository,
                           UsmUserGroupRepository usmUserGroupRepository,
                           UsmGroupRepository usmGroupRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.usmUserRepository = usmUserRepository;
        this.usmUserGroupRepository = usmUserGroupRepository;
        this.usmGroupRepository = usmGroupRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<AuthUserDto> authenticate(String loginId, String password) {
        return authenticate(loginId, password, defaultAppId);
    }

    @Override
    @Transactional
    public Optional<AuthUserDto> authenticate(String loginId, String password, String appId) {
        Optional<UsmUser> optUser = appId != null && !appId.isBlank()
            ? usmUserRepository.findByLoginIdAndAppId(loginId, appId)
            : usmUserRepository.findByLoginId(loginId);
        if (optUser.isEmpty()) {
            optUser = appId != null && !appId.isBlank()
                ? usmUserRepository.findByLoginIdIgnoreCaseAndAppId(loginId, appId)
                : usmUserRepository.findByLoginIdIgnoreCase(loginId);
        }
        if (optUser.isEmpty()) {
            optUser = usmUserRepository.findByLoginIdIgnoreCase(loginId);
        }
        if (optUser.isEmpty()) return Optional.empty();
        UsmUser user = optUser.get();
        if (user.getWhenDeleted() != null) return Optional.empty();
        if (user.getIsActive() == null || user.getIsActive().compareTo(BigDecimal.ONE) != 0) return Optional.empty();

        LocalDateTime now = LocalDateTime.now();
        if (user.getPwdLockedUntil() != null && user.getPwdLockedUntil().isAfter(now)) {
            throw new BusinessValidationException(
                "Account temporarily locked due to failed login attempts. Try again after 5 minutes.");
        }
        if (user.getPwdLockedUntil() != null && !user.getPwdLockedUntil().isAfter(now)) {
            user.setPwdLockedUntil(null);
            user.setPwdRetryCount(BigDecimal.ZERO);
            usmUserRepository.save(user);
        }

        String stored = user.getPassword();
        if (stored == null || stored.isBlank()) return Optional.empty();
        // BCrypt only — no plaintext fallback
        boolean matches = passwordEncoder.matches(password, stored);
        if (!matches) {
            recordFailedLogin(user);
            return Optional.empty();
        }

        user.setPwdRetryCount(BigDecimal.ZERO);
        user.setPwdLockedUntil(null);
        user.setUpdatedOn(now);
        usmUserRepository.save(user);

        List<String> roles = getRolesForUser(loginId);
        return Optional.of(new AuthUserDto(loginId, user.getFullName(), roles));
    }

    private void recordFailedLogin(UsmUser user) {
        int retries = user.getPwdRetryCount() != null ? user.getPwdRetryCount().intValue() : 0;
        retries++;
        user.setPwdRetryCount(BigDecimal.valueOf(retries));
        user.setUpdatedOn(LocalDateTime.now());
        if (retries >= PasswordRules.MAX_FAILED_ATTEMPTS) {
            user.setPwdLockedUntil(LocalDateTime.now().plusMinutes(PasswordRules.LOCK_MINUTES));
            user.setPwdRetryCount(BigDecimal.ZERO);
            usmUserRepository.save(user);
            throw new BusinessValidationException(
                "Account temporarily locked due to failed login attempts. Try again after 5 minutes.");
        }
        usmUserRepository.save(user);
    }

    private void requireStrongPassword(String password) {
        try {
            PasswordRules.validateStrength(password);
        } catch (IllegalArgumentException ex) {
            throw new BusinessValidationException(ex.getMessage());
        }
    }

    @Override
    public List<String> getRolesForUser(String loginId) {
        List<UsmUserGroup> userGroups = usmUserGroupRepository.findByLoginId(loginId);
        if (userGroups.isEmpty()) {
            userGroups = usmUserGroupRepository.findByLoginIdIgnoreCase(loginId);
        }
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        for (UsmUserGroup ug : userGroups) {
            if (ug.getGroupId() != null) {
                usmGroupRepository.findByGroupId(ug.getGroupId()).ifPresent(g -> {
                    if (g.getWhenDeleted() == null && g.getIsActive() != null && g.getIsActive().compareTo(BigDecimal.ONE) == 0) {
                        if (g.getGroupId() != null && !g.getGroupId().isBlank()) roles.add(g.getGroupId());
                        if (g.getGroupName() != null && !g.getGroupName().isBlank()) roles.add(g.getGroupName());
                    }
                });
            }
        }
        if (roles.isEmpty() && loginId != null && "admin".equalsIgnoreCase(loginId)) {
            roles.add("ADMIN");
        }
        return new ArrayList<>(roles);
    }

    @Override
    @Transactional
    public UsmUser createUser(String loginId, String password, String fullName, String appId, List<String> groupIds) {
        if (loginId == null || loginId.isBlank())
            throw new BusinessValidationException("loginId is required");
        requireStrongPassword(password);
        if (usmUserRepository.existsByLoginId(loginId))
            throw new DuplicateResourceException("User", loginId);
        UsmUser user = new UsmUser();
        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setAppId(appId != null && !appId.isBlank() ? appId : defaultAppId);
        user.setIsActive(BigDecimal.ONE);
        user.setPwdRetryCount(BigDecimal.ZERO);
        user.setPwdLockedUntil(null);
        user.setPwdUpdatedOn(LocalDateTime.now());
        user.setCreatedOn(LocalDateTime.now());
        user.setUpdatedOn(LocalDateTime.now());
        UsmUser saved = usmUserRepository.save(user);
        if (groupIds != null && !groupIds.isEmpty()) {
            for (String groupId : groupIds) {
                UsmUserGroup ug = new UsmUserGroup();
                ug.setGroupId(groupId);
                ug.setLoginId(loginId);
                ug.setCreatedOn(LocalDateTime.now());
                ug.setUpdatedOn(LocalDateTime.now());
                usmUserGroupRepository.save(ug);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public UsmUser updateUser(String loginId, String fullName, String password, Boolean isActive, List<String> groupIds) {
        UsmUser user = usmUserRepository.findByLoginId(loginId)
            .orElseThrow(() -> new ResourceNotFoundException("User", loginId));
        if (fullName != null) user.setFullName(fullName);
        if (password != null && !password.isBlank()) {
            requireStrongPassword(password);
            user.setPassword(passwordEncoder.encode(password));
            user.setPwdUpdatedOn(LocalDateTime.now());
            user.setPwdRetryCount(BigDecimal.ZERO);
            user.setPwdLockedUntil(null);
        }
        if (isActive != null) user.setIsActive(isActive ? BigDecimal.ONE : BigDecimal.ZERO);
        user.setUpdatedOn(LocalDateTime.now());
        if (groupIds != null) {
            List<UsmUserGroup> existing = usmUserGroupRepository.findByLoginId(loginId);
            for (UsmUserGroup ug : existing) usmUserGroupRepository.delete(ug);
            for (String groupId : groupIds) {
                UsmUserGroup ug = new UsmUserGroup();
                ug.setGroupId(groupId);
                ug.setLoginId(loginId);
                ug.setCreatedOn(LocalDateTime.now());
                ug.setUpdatedOn(LocalDateTime.now());
                usmUserGroupRepository.save(ug);
            }
        }
        return usmUserRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String loginId, String currentPassword, String newPassword) {
        if (loginId == null || loginId.isBlank()) {
            throw new BusinessValidationException("User is required");
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new BusinessValidationException("Current password is required");
        }
        requireStrongPassword(newPassword);
        if (currentPassword.equals(newPassword)) {
            throw new BusinessValidationException("New password must be different from current password");
        }

        UsmUser user = usmUserRepository.findByLoginId(loginId)
            .or(() -> usmUserRepository.findByLoginIdIgnoreCase(loginId))
            .orElseThrow(() -> new ResourceNotFoundException("User", loginId));

        String stored = user.getPassword();
        if (stored == null || !passwordEncoder.matches(currentPassword, stored)) {
            throw new BusinessValidationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPwdUpdatedOn(LocalDateTime.now());
        user.setPwdRetryCount(BigDecimal.ZERO);
        user.setPwdLockedUntil(null);
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(loginId);
        usmUserRepository.save(user);
    }

    @Override
    public UsmUser getByLoginId(String loginId) {
        return usmUserRepository.findByLoginId(loginId)
            .orElseThrow(() -> new ResourceNotFoundException("User", loginId));
    }

    @Override
    public List<UsmUser> findAll() {
        return usmUserRepository.findAll().stream()
            .filter(u -> u.getWhenDeleted() == null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByLoginId(String loginId) {
        UsmUser user = usmUserRepository.findByLoginId(loginId)
            .orElseThrow(() -> new ResourceNotFoundException("User", loginId));
        user.setWhenDeleted(LocalDateTime.now());
        user.setIsActive(BigDecimal.ZERO);
        usmUserRepository.save(user);
    }

    @Override
    public UserResponse getUserResponse(String loginId) {
        UsmUser user = getByLoginId(loginId);
        UserResponse res = userMapper.toResponse(user);
        res.setEmailAddress(user.getEmailAddress());
        List<UsmUserGroup> userGroups = usmUserGroupRepository.findByLoginId(loginId);
        List<String> roleIds = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        for (UsmUserGroup ug : userGroups) {
            if (ug.getGroupId() != null) {
                roleIds.add(ug.getGroupId());
                usmGroupRepository.findByGroupId(ug.getGroupId()).ifPresent(g ->
                    roleNames.add(g.getGroupName() != null ? g.getGroupName() : g.getGroupId()));
            }
        }
        res.setRoleIds(roleIds);
        res.setRoleNames(roleNames);
        return res;
    }

    @Override
    public List<UserResponse> findAllResponses() {
        return findAll().stream().map(u -> getUserResponse(u.getLoginId())).collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleIdsByUserId(Long userId) {
        UsmUser user = usmUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        return usmUserGroupRepository.findByLoginId(user.getLoginId()).stream()
            .map(UsmUserGroup::getGroupId)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, String groupId) {
        UsmUser user = usmUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        if (groupId == null || groupId.isBlank()) throw new BusinessValidationException("groupId is required");
        usmGroupRepository.findByGroupId(groupId)
            .orElseThrow(() -> new ResourceNotFoundException("Role", groupId));
        boolean alreadyAssigned = usmUserGroupRepository.findByLoginId(user.getLoginId()).stream()
            .anyMatch(ug -> groupId.equals(ug.getGroupId()));
        if (alreadyAssigned) return;
        UsmUserGroup ug = new UsmUserGroup();
        ug.setGroupId(groupId);
        ug.setLoginId(user.getLoginId());
        ug.setCreatedOn(LocalDateTime.now());
        ug.setUpdatedOn(LocalDateTime.now());
        usmUserGroupRepository.save(ug);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, String groupId) {
        UsmUser user = usmUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        List<UsmUserGroup> userGroups = usmUserGroupRepository.findByLoginId(user.getLoginId());
        for (UsmUserGroup ug : userGroups) {
            if (groupId != null && groupId.equals(ug.getGroupId())) {
                usmUserGroupRepository.delete(ug);
            }
        }
    }

    @Override
    public UserResponse getById(Long id) {
        UsmUser user = usmUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(id)));
        return getUserResponse(user.getLoginId());
    }

    @Override
    public void deleteById(Long id) {
        UsmUser user = usmUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(id)));
        deleteByLoginId(user.getLoginId());
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        UsmUser user = usmUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(id)));
        updateUser(
            user.getLoginId(),
            request.getFullName(),
            request.getPassword(),
            request.getActive(),
            request.getGroupIds()
        );
        return getUserResponse(user.getLoginId());
    }
}
