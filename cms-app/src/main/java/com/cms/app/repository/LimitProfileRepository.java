package com.cms.app.repository;

import com.cms.app.entity.LimitProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimitProfileRepository extends JpaRepository<LimitProfile, Long> {
    Optional<LimitProfile> findByProfileCode(String profileCode);
}
