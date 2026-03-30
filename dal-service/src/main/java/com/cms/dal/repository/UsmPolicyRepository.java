package com.cms.dal.repository;

import com.cms.dal.entity.UsmPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsmPolicyRepository extends JpaRepository<UsmPolicy, Long> {

    Optional<UsmPolicy> findByPolicyId(String policyId);
}
