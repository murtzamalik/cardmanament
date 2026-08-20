package com.cms.app.repository;

import com.cms.app.entity.CardRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {
    Optional<CardRequest> findTopByRelationshipNumAndIsProcessedOrderByCreatedOnDesc(String relationshipNum, Integer isProcessed);
}
