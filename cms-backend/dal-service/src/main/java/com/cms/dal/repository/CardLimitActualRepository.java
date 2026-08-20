package com.cms.dal.repository;

import com.cms.dal.entity.CardLimitActual;
import com.cms.dal.entity.CardLimitActualId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardLimitActualRepository extends JpaRepository<CardLimitActual, CardLimitActualId> {

    List<CardLimitActual> findByPan(String pan);

    List<CardLimitActual> findByPanAndTranCode(String pan, String tranCode);

    Optional<CardLimitActual> findByPanAndChannelCodeAndTranCode(String pan, String channelCode, String tranCode);

    boolean existsByPanAndChannelCodeAndTranCode(String pan, String channelCode, String tranCode);
}
