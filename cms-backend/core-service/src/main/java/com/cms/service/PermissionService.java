package com.cms.service;

import com.cms.dto.request.PermissionCreateRequest;
import com.cms.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {

    PermissionResponse create(PermissionCreateRequest request);

    List<PermissionResponse> findAll();

    PermissionResponse getById(Long id);
}
