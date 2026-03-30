package com.cms.dal.repository;

import com.cms.dal.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    java.util.Optional<Account> findByAccountNum(String accountNum);
}
