package com.cms.dal.repository;

import com.cms.dal.entity.UsmPwdHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsmPwdHistoryRepository extends JpaRepository<UsmPwdHistory, String> {

    List<UsmPwdHistory> findByLoginIdOrderByCreatedOnDesc(String loginId, Pageable pageable);
}
