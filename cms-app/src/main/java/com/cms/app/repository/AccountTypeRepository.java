package com.cms.app.repository;

import com.cms.app.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    Optional<AccountType> findByAcctTypeCode(String acctTypeCode);
}
