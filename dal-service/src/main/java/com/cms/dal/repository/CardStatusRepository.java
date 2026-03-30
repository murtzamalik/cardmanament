package com.cms.dal.repository;

import com.cms.dal.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardStatusRepository extends JpaRepository<CardStatus, Long> {

    List<CardStatus> findAllByOrderByCardStatusCode();

    java.util.Optional<CardStatus> findByCardStatusCode(String cardStatusCode);
}
