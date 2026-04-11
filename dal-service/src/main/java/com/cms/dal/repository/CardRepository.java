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

    @Query("SELECT c FROM Card c LEFT JOIN FETCH c.branch LEFT JOIN FETCH c.cardStatus LEFT JOIN FETCH c.cardType LEFT JOIN FETCH c.cardProduct WHERE c.cardId = :id")
    Optional<Card> findByIdWithDetails(@Param("id") Long id);

    Optional<Card> findByPan(String pan);

    Optional<Card> findByPanHash(String panHash);

    List<Card> findByRelationshipNum(String relationshipNum);

    List<Card> findByBranchCode(String branchCode);

    List<Card> findByCardStatusCode(String cardStatusCode);

    List<Card> findByRelationshipNumAndCardStatusCode(String relationshipNum, String cardStatusCode);

    Page<Card> findByRelationshipNum(String relationshipNum, Pageable pageable);

    Page<Card> findByBranchCode(String branchCode, Pageable pageable);

    Page<Card> findByCardStatusCode(String cardStatusCode, Pageable pageable);

    List<Card> findByExpiryDateBetween(LocalDateTime from, LocalDateTime to);
}
