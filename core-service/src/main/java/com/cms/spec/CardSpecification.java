package com.cms.spec;

import com.cms.dal.entity.Card;
import com.cms.dto.request.CardSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class CardSpecification {

    /** Use with fromSearch() so list/card responses get type, status, product, branch names. */
    public static Specification<Card> fetchTypeStatusProductBranch() {
        return (root, query, cb) -> {
            if (query.getResultType() == Card.class) {
                root.fetch("branch", JoinType.LEFT);
                root.fetch("cardStatus", JoinType.LEFT);
                root.fetch("cardType", JoinType.LEFT);
                root.fetch("cardProduct", JoinType.LEFT);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Card> fromSearch(CardSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (req.getPan() != null && !req.getPan().isBlank()) {
                String digits = req.getPan().replaceAll("\\D", "");
                String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : req.getPan().trim();
                if (last4.length() >= 4) {
                    predicates.add(cb.equal(root.get("panLast4"), last4));
                }
            }
            if (req.getRelationshipNum() != null && !req.getRelationshipNum().isBlank()) {
                predicates.add(cb.equal(root.get("relationshipNum"), req.getRelationshipNum()));
            }
            if (req.getBranchId() != null) {
                predicates.add(cb.equal(root.get("branchId"), req.getBranchId()));
            } else if (req.getBranchCode() != null && !req.getBranchCode().isBlank()) {
                predicates.add(cb.equal(root.get("branchCode"), req.getBranchCode()));
            }
            if (req.getCardStatusId() != null) {
                predicates.add(cb.equal(root.get("cardStatusId"), req.getCardStatusId()));
            } else if (req.getCardStatusCode() != null && !req.getCardStatusCode().isBlank()) {
                predicates.add(cb.equal(root.get("cardStatusCode"), req.getCardStatusCode()));
            }
            if (req.getProductCode() != null && !req.getProductCode().isBlank()) {
                predicates.add(cb.equal(root.get("productCode"), req.getProductCode()));
            }
            if (req.getCardTypeCode() != null && !req.getCardTypeCode().isBlank()) {
                predicates.add(cb.equal(root.get("cardTypeCode"), req.getCardTypeCode()));
            }
            if (req.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdOn"), req.getDateFrom()));
            }
            if (req.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdOn"), req.getDateTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
