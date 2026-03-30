package com.cms.dal.repository;

import com.cms.dal.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByCountryCode(String countryCode);

    java.util.Optional<Branch> findByBranchCode(String branchCode);
}
