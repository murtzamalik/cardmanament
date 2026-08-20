package com.cms.app.repository;

import com.cms.app.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardStatusRepository extends JpaRepository<CardStatus, String> {
    Optional<CardStatus> findByCardStatusCode(String cardStatusCode);
}
