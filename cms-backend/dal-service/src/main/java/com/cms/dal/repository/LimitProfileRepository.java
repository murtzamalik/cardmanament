package com.cms.dal.repository;

import com.cms.dal.entity.LimitProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LimitProfileRepository extends JpaRepository<LimitProfile, Long> {

    List<LimitProfile> findByIsActiveOrderByProfileCodeAsc(Integer isActive);

    java.util.Optional<LimitProfile> findByProfileCode(String profileCode);
}
