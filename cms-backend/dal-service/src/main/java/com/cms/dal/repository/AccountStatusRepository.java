package com.cms.dal.repository;

import com.cms.dal.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountStatusRepository extends JpaRepository<AccountStatus, Long> {

    Optional<AccountStatus> findByAcctStatusCode(String acctStatusCode);

    boolean existsByAcctStatusNameIgnoreCase(String acctStatusName);

    List<AccountStatus> findAllByOrderByAcctStatusCodeAsc();
}
