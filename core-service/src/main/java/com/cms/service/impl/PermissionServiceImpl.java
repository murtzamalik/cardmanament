package com.cms.service.impl;

import com.cms.dal.entity.UsmPermission;
import com.cms.dal.repository.UsmPermissionRepository;
import com.cms.dto.request.PermissionCreateRequest;
import com.cms.dto.response.PermissionResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.PermissionMapper;
import com.cms.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final UsmPermissionRepository usmPermissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(UsmPermissionRepository usmPermissionRepository, PermissionMapper permissionMapper) {
        this.usmPermissionRepository = usmPermissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    @Transactional
    public PermissionResponse create(PermissionCreateRequest request) {
        if (request.getPermissionId() != null && usmPermissionRepository.findByPermissionId(request.getPermissionId()).isPresent())
            throw new DuplicateResourceException("Permission", request.getPermissionId());
        UsmPermission p = new UsmPermission();
        p.setPermissionId(request.getPermissionId());
        p.setPerParentId(request.getPerParentId());
        p.setPermissionName(request.getPermissionName());
        p.setPermissionType(request.getPermissionType());
        p.setCreatedOn(LocalDateTime.now());
        p.setUpdatedOn(LocalDateTime.now());
        return permissionMapper.toResponse(usmPermissionRepository.save(p));
    }

    @Override
    public List<PermissionResponse> findAll() {
        return permissionMapper.toResponseList(usmPermissionRepository.findAll());
    }

    @Override
    public PermissionResponse getById(Long id) {
        UsmPermission p = usmPermissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", String.valueOf(id)));
        return permissionMapper.toResponse(p);
    }
}
