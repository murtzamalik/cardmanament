package com.cms.dal.repository;

import com.cms.dal.entity.CardLimitCustomized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardLimitCustomizedRepository extends JpaRepository<CardLimitCustomized, Long> {

    List<CardLimitCustomized> findByPan(String pan);

    List<CardLimitCustomized> findByPanAndIsActive(String pan, Integer isActive);

    Optional<CardLimitCustomized> findByPanAndTranCode(String pan, String tranCode);

    Optional<CardLimitCustomized> findByPanAndTranCodeAndIsActive(String pan, String tranCode, Integer isActive);

    boolean existsByPanAndTranCode(String pan, String tranCode);
}
