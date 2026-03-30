package com.cms.dal.repository;

import com.cms.dal.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType, Long> {

    Optional<CardType> findByCardTypeCode(String cardTypeCode);

    List<CardType> findByProductCode(String productCode);

    List<CardType> findByProductCodeAndIsActive(String productCode, Integer isActive);

    List<CardType> findAllByOrderByCardTypeCode();
}
