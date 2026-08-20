package com.cms.app.repository;

import com.cms.app.entity.CardProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardProductionStatusRepository extends JpaRepository<CardProductionStatus, String> {
    Optional<CardProductionStatus> findByCardProdStatusId(String cardProdStatusId);
}
