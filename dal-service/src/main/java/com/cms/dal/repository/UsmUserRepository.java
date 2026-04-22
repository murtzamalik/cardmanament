package com.cms.dal.repository;

import com.cms.dal.entity.UsmUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsmUserRepository extends JpaRepository<UsmUser, Long> {

    Optional<UsmUser> findByLoginIdAndAppId(String loginId, String appId);

    Optional<UsmUser> findByLoginIdIgnoreCaseAndAppId(String loginId, String appId);

    Optional<UsmUser> findByLoginId(String loginId);

    Optional<UsmUser> findByLoginIdIgnoreCase(String loginId);

    boolean existsByLoginId(String loginId);
}
