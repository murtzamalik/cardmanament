package com.cms.dal.repository;

import com.cms.dal.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

    Optional<AccountType> findByAcctTypeCode(String acctTypeCode);

    boolean existsByAcctTypeNameIgnoreCase(String acctTypeName);
}
