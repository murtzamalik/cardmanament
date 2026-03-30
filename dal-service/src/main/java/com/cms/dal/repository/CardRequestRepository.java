package com.cms.dal.repository;

import com.cms.dal.entity.CardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    List<CardRequest> findByRelationshipNum(String relationshipNum);

    List<CardRequest> findByRelationshipNumAndAccountNum(String relationshipNum, String accountNum);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    List<CardRequest> findByBranchCode(String branchCode);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    List<CardRequest> findByIsProcessed(Integer isProcessed);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    List<CardRequest> findByProgressFlag(Integer progressFlag);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    Page<CardRequest> findByRelationshipNum(String relationshipNum, Pageable pageable);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    Page<CardRequest> findByBranchCode(String branchCode, Pageable pageable);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    Page<CardRequest> findByIsProcessed(Integer isProcessed, Pageable pageable);

    @EntityGraph(attributePaths = {"cardType", "cardProduct", "branch"})
    @Query("SELECT cr FROM CardRequest cr")
    Page<CardRequest> findAllWithDetails(Pageable pageable);

    @Query("SELECT cr FROM CardRequest cr LEFT JOIN FETCH cr.cardType LEFT JOIN FETCH cr.cardProduct LEFT JOIN FETCH cr.branch WHERE cr.requestId = :requestId")
    Optional<CardRequest> findByRequestIdWithDetails(@Param("requestId") Long requestId);
}
