package com.cms.dal.repository;

import com.cms.dal.entity.CardAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardAccountRepository extends JpaRepository<CardAccount, Long> {

    List<CardAccount> findByPan(String pan);

    List<CardAccount> findByAccountNum(String accountNum);

    List<CardAccount> findByRelationshipNum(String relationshipNum);

    List<CardAccount> findByPanAndAccountNum(String pan, String accountNum);

    boolean existsByPanAndAccountNum(String pan, String accountNum);
}
