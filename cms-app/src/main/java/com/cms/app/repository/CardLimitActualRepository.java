package com.cms.app.repository;

import com.cms.app.entity.CardLimitActual;
import com.cms.app.entity.CardLimitActualId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardLimitActualRepository extends JpaRepository<CardLimitActual, CardLimitActualId> {

    Optional<CardLimitActual> findByPanAndChannelCodeAndTranCode(String pan, String channelCode, String tranCode);

    List<CardLimitActual> findByPanAndChannelCode(String pan, String channelCode);
}
