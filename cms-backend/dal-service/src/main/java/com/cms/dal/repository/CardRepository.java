package com.cms.dal.repository;

import com.cms.dal.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @Query("SELECT c FROM Card c WHERE c.cardId = :id")
    Optional<Card> findByIdWithDetails(@Param("id") Long id);

    Optional<Card> findByPan(String pan);

    Optional<Card> findByPanHash(String panHash);

    List<Card> findByCardProdStatusIdAndCardTypeCode(String cardProdStatusId, String cardTypeCode);

    List<Card> findByRelationshipNum(String relationshipNum);

    List<Card> findByBranchCode(String branchCode);

    List<Card> findByCardStatusCode(String cardStatusCode);

    List<Card> findByRelationshipNumAndCardStatusCode(String relationshipNum, String cardStatusCode);

    Page<Card> findByRelationshipNum(String relationshipNum, Pageable pageable);

    Page<Card> findByBranchCode(String branchCode, Pageable pageable);

    Page<Card> findByCardStatusCode(String cardStatusCode, Pageable pageable);

    List<Card> findByExpiryDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.whenDeleted IS NULL AND (UPPER(TRIM(c.cardStatusCode)) = 'HOT' OR TRIM(c.cardStatusCode) = '003')")
    long countHotCards();

    @Query("SELECT COUNT(c) FROM Card c WHERE c.whenDeleted IS NULL AND ("
            + "(c.issuedDate IS NOT NULL AND c.issuedDate >= :from AND c.issuedDate < :to) OR "
            + "(c.issuedDate IS NULL AND c.createdOn IS NOT NULL AND c.createdOn >= :from AND c.createdOn < :to))")
    long countIssuedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.whenDeleted IS NULL AND c.expiryDate IS NOT NULL "
            + "AND c.expiryDate >= :from AND c.expiryDate <= :to")
    long countExpiringBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT c.cardStatusCode, COUNT(c) FROM Card c WHERE c.whenDeleted IS NULL GROUP BY c.cardStatusCode")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT c FROM Card c WHERE c.whenDeleted IS NULL AND c.expiryDate IS NOT NULL "
            + "AND c.expiryDate >= :from AND c.expiryDate <= :to ORDER BY c.expiryDate ASC")
    List<Card> findExpiringBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}