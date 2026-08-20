package com.cms.dal.repository;

import com.cms.dal.entity.BizProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BizProcessRepository extends JpaRepository<BizProcess, Long> {

    @Query("SELECT DISTINCT b FROM BizProcess b LEFT JOIN FETCH b.bizProcessStates ORDER BY b.bizProcessId")
    List<BizProcess> findAllWithStates();

    @Query("SELECT b FROM BizProcess b LEFT JOIN FETCH b.bizProcessStates WHERE b.channelId = :channelId AND b.messageType = :messageType")
    Optional<BizProcess> findByChannelIdAndMessageType(@Param("channelId") Long channelId, @Param("messageType") Integer messageType);
}
