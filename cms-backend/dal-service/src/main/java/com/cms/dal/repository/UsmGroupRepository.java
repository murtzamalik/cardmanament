package com.cms.dal.repository;

import com.cms.dal.entity.UsmGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsmGroupRepository extends JpaRepository<UsmGroup, Long> {

    List<UsmGroup> findByAppId(String appId);

    java.util.Optional<UsmGroup> findByGroupId(String groupId);
}
