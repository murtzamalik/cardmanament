package com.cms.app.repository;

import com.cms.app.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByPan(String pan);

    List<Card> findByRelationshipNumOrderByCreatedOnDesc(String relationshipNum);

    Optional<Card> findFirstByRelationshipNumOrderByCreatedOnDesc(String relationshipNum);

    Optional<Card> findByRelationshipNumAndPan(String relationshipNum, String pan);
}
