package com.cms.app.repository;

import com.cms.app.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountStatusRepository extends JpaRepository<AccountStatus, Long> {
    Optional<AccountStatus> findByAcctStatusCode(String acctStatusCode);
}
