package com.cms.dal.repository;

import com.cms.dal.entity.UsmPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsmPermissionRepository extends JpaRepository<UsmPermission, Long> {

    Optional<UsmPermission> findByPermissionId(String permissionId);

    List<UsmPermission> findAllByOrderByPermissionId();
}
