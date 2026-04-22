package com.cms.service;

import com.cms.common.dto.AuthUserDto;
import com.cms.dal.entity.UsmUser;
import com.cms.dto.request.UserUpdateRequest;
import com.cms.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<AuthUserDto> authenticate(String loginId, String password);

    Optional<AuthUserDto> authenticate(String loginId, String password, String appId);

    List<String> getRolesForUser(String loginId);

    UsmUser createUser(String loginId, String password, String fullName, String appId, List<String> groupIds);

    UsmUser updateUser(String loginId, String fullName, String password, Boolean isActive, List<String> groupIds);

    UsmUser getByLoginId(String loginId);

    UserResponse getById(Long id);

    List<UsmUser> findAll();

    void deleteByLoginId(String loginId);

    void deleteById(Long id);

    UserResponse update(Long id, UserUpdateRequest request);

    UserResponse getUserResponse(String loginId);

    List<UserResponse> findAllResponses();

    List<String> getRoleIdsByUserId(Long userId);

    void assignRoleToUser(Long userId, String groupId);

    void removeRoleFromUser(Long userId, String groupId);
}
