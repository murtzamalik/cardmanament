package com.cms.service;

import com.cms.dto.request.RoleCreateRequest;
import com.cms.dto.request.RoleUpdateRequest;
import com.cms.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse create(RoleCreateRequest request);

    List<RoleResponse> findAll();

    RoleResponse getById(Long id);

    RoleResponse update(Long id, RoleUpdateRequest request);

    void delete(Long id);
}
