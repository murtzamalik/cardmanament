package com.cms.dal.repository;

import com.cms.dal.entity.CardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {

    List<CardRequest> findByRelationshipNum(String relationshipNum);

    List<CardRequest> findByRelationshipNumAndAccountNum(String relationshipNum, String accountNum);

    List<CardRequest> findByBranchCode(String branchCode);

    List<CardRequest> findByIsProcessed(Integer isProcessed);

    List<CardRequest> findByProgressFlag(Integer progressFlag);

    boolean existsBySourceCardIdAndRequestTypeIdAndIsProcessed(
            Long sourceCardId, String requestTypeId, Integer isProcessed);

    long countByProgressFlag(Integer progressFlag);

    long countByIsProcessed(Integer isProcessed);

    @Query("SELECT cr.progressFlag, COUNT(cr) FROM CardRequest cr GROUP BY cr.progressFlag")
    List<Object[]> countGroupedByProgressFlag();

    Page<CardRequest> findByRelationshipNum(String relationshipNum, Pageable pageable);

    Page<CardRequest> findByBranchCode(String branchCode, Pageable pageable);

    Page<CardRequest> findByIsProcessed(Integer isProcessed, Pageable pageable);

    @Query("""
        SELECT cr FROM CardRequest cr
        WHERE (:relationshipNum IS NULL OR cr.relationshipNum = :relationshipNum)
          AND (:branchCode IS NULL OR cr.branchCode = :branchCode)
          AND (:isProcessed IS NULL OR cr.isProcessed = :isProcessed)
          AND (:requestTypeId IS NULL OR UPPER(cr.requestTypeId) = UPPER(:requestTypeId))
        """)
    Page<CardRequest> search(
            @Param("relationshipNum") String relationshipNum,
            @Param("branchCode") String branchCode,
            @Param("isProcessed") Integer isProcessed,
            @Param("requestTypeId") String requestTypeId,
            Pageable pageable);

    @Query("SELECT cr FROM CardRequest cr")
    Page<CardRequest> findAllWithDetails(Pageable pageable);

    Optional<CardRequest> findByRequestId(Long requestId);

    default Optional<CardRequest> findByRequestIdWithDetails(Long requestId) {
        return findByRequestId(requestId);
    }
}
