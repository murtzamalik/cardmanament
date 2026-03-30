package com.cms.service.impl;

import com.cms.dal.entity.UsmGroup;
import com.cms.dal.repository.UsmGroupRepository;
import com.cms.dto.request.RoleCreateRequest;
import com.cms.dto.request.RoleUpdateRequest;
import com.cms.dto.response.RoleResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.RoleMapper;
import com.cms.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final UsmGroupRepository usmGroupRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(UsmGroupRepository usmGroupRepository, RoleMapper roleMapper) {
        this.usmGroupRepository = usmGroupRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public RoleResponse create(RoleCreateRequest request) {
        if (request.getGroupId() != null && usmGroupRepository.findByGroupId(request.getGroupId()).isPresent())
            throw new DuplicateResourceException("Role", request.getGroupId());
        UsmGroup g = new UsmGroup();
        g.setGroupId(request.getGroupId());
        g.setGroupName(request.getGroupName());
        g.setIsActive(request.getActive() != null && request.getActive() ? BigDecimal.ONE : BigDecimal.ZERO);
        g.setCreatedOn(LocalDateTime.now());
        g.setUpdatedOn(LocalDateTime.now());
        return roleMapper.toResponse(usmGroupRepository.save(g));
    }

    @Override
    public List<RoleResponse> findAll() {
        List<UsmGroup> list = usmGroupRepository.findAll().stream()
            .filter(g -> g.getWhenDeleted() == null)
            .collect(Collectors.toList());
        return roleMapper.toResponseList(list);
    }

    @Override
    public RoleResponse getById(Long id) {
        UsmGroup g = usmGroupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", String.valueOf(id)));
        return roleMapper.toResponse(g);
    }

    @Override
    @Transactional
    public RoleResponse update(Long id, RoleUpdateRequest request) {
        UsmGroup g = usmGroupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", String.valueOf(id)));
        if (request.getGroupName() != null) g.setGroupName(request.getGroupName());
        if (request.getActive() != null) g.setIsActive(request.getActive() ? BigDecimal.ONE : BigDecimal.ZERO);
        g.setUpdatedOn(LocalDateTime.now());
        return roleMapper.toResponse(usmGroupRepository.save(g));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!usmGroupRepository.existsById(id))
            throw new ResourceNotFoundException("Role", String.valueOf(id));
        usmGroupRepository.deleteById(id);
    }
}
