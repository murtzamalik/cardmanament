package com.cms.dal.repository;

import com.cms.dal.entity.UsmUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsmUserGroupRepository extends JpaRepository<UsmUserGroup, Long> {

    List<UsmUserGroup> findByLoginId(String loginId);
}
