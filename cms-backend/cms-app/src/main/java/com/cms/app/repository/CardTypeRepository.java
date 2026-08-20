package com.cms.app.repository;

import com.cms.app.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType, String> {
    Optional<CardType> findByCardTypeCode(String cardTypeCode);

    List<CardType> findByIsActive(Integer isActive);
}
