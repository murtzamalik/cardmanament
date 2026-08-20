package com.cms.common.service;

import com.cms.dal.entity.UsmPermission;
import com.cms.dal.repository.UsmPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads and exposes permissions for display and audit. Maps to JWT roles in security layer.
 */
@Service
public class PermissionControllerService {

    private final UsmPermissionRepository usmPermissionRepository;

    public PermissionControllerService(UsmPermissionRepository usmPermissionRepository) {
        this.usmPermissionRepository = usmPermissionRepository;
    }

    /**
     * All permissions for display (e.g. used by AddForAuditLogs to resolve PermissionId to PermissionName).
     */
    public List<UsmPermission> getAllPermissionsForDisplay() {
        return usmPermissionRepository.findAllByOrderByPermissionId();
    }

    public Optional<UsmPermission> getPermissionById(String permissionId) {
        return usmPermissionRepository.findByPermissionId(permissionId);
    }
}
