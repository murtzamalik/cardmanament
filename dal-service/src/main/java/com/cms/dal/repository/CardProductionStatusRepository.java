package com.cms.dal.repository;

import com.cms.dal.entity.CardProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardProductionStatusRepository extends JpaRepository<CardProductionStatus, Long> {

    List<CardProductionStatus> findAllByOrderByCardProdStatusId();
}
