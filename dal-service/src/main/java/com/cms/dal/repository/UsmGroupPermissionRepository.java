package com.cms.dal.repository;

import com.cms.dal.entity.UsmGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsmGroupPermissionRepository extends JpaRepository<UsmGroupPermission, Long> {

    List<UsmGroupPermission> findByGroupId(String groupId);
}
