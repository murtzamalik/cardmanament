package com.cms.dal.repository;

import com.cms.dal.entity.BizProcessStates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BizProcessStatesRepository extends JpaRepository<BizProcessStates, Long> {

    List<BizProcessStates> findByBizProcessIdOrderBySequenceNumberAsc(Long bizProcessId);
}
