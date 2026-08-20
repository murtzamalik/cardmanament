package com.cms.app.repository;

import com.cms.app.entity.CardLimitCustomized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardLimitCustomizedRepository extends JpaRepository<CardLimitCustomized, Long> {

    Optional<CardLimitCustomized> findByPanAndTranCodeAndIsActive(String pan, String tranCode, Integer isActive);
}
