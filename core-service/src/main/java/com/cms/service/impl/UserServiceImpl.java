package com.cms.service.impl;

import com.cms.common.dto.AuthUserDto;
import com.cms.dal.entity.UsmGroup;
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
    public Optional<AuthUserDto> authenticate(String loginId, String password, String appId) {
        Optional<UsmUser> optUser = appId != null && !appId.isBlank()
            ? usmUserRepository.findByLoginIdAndAppId(loginId, appId)
            : usmUserRepository.findByLoginId(loginId);
        if (optUser.isEmpty()) return Optional.empty();
        UsmUser user = optUser.get();
        if (user.getWhenDeleted() != null) return Optional.empty();
        if (user.getIsActive() == null || user.getIsActive().compareTo(BigDecimal.ONE) != 0) return Optional.empty();
        String stored = user.getPassword();
        if (stored == null) return Optional.empty();
        boolean matches = passwordEncoder.matches(password, stored)
            || stored.equals(password); // allow plain during migration
        if (!matches) return Optional.empty();
        List<String> roles = getRolesForUser(loginId);
        return Optional.of(new AuthUserDto(loginId, user.getFullName(), roles));
    }

    @Override
    public List<String> getRolesForUser(String loginId) {
        List<UsmUserGroup> userGroups = usmUserGroupRepository.findByLoginId(loginId);
        List<String> roles = new ArrayList<>();
        for (UsmUserGroup ug : userGroups) {
            if (ug.getGroupId() != null) {
                usmGroupRepository.findByGroupId(ug.getGroupId()).ifPresent(g -> {
                    if (g.getWhenDeleted() == null && g.getIsActive() != null && g.getIsActive().compareTo(BigDecimal.ONE) == 0)
                        roles.add(g.getGroupName() != null ? g.getGroupName() : g.getGroupId());
                });
            }
        }
        return roles;
    }

    @Override
    @Transactional
    public UsmUser createUser(String loginId, String password, String fullName, String appId, List<String> groupIds) {
        if (loginId == null || loginId.isBlank())
            throw new BusinessValidationException("loginId is required");
        if (usmUserRepository.existsByLoginId(loginId))
            throw new DuplicateResourceException("User", loginId);
        UsmUser user = new UsmUser();
        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password != null ? password : ""));
        user.setFullName(fullName);
        user.setAppId(appId != null ? appId : defaultAppId);
        user.setIsActive(BigDecimal.ONE);
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
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
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
                usmGroupRepository.findByGroupId(ug.getGroupId()).ifPresent(g -> roleNames.add(g.getGroupName() != null ? g.getGroupName() : g.getGroupId()));
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
