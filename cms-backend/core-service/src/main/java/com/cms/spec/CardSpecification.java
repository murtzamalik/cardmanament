package com.cms.spec;

import com.cms.dal.entity.Card;
import com.cms.dto.request.CardSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class CardSpecification {

    /** Relationships removed — code-based PKs used instead of surrogate IDs. */
    public static Specification<Card> fetchTypeStatusProductBranch() {
        return (root, query, cb) -> cb.conjunction();
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
                // branchId removed from Card entity — skip, use branchCode instead
            } else if (req.getBranchCode() != null && !req.getBranchCode().isBlank()) {
                predicates.add(cb.equal(root.get("branchCode"), req.getBranchCode()));
            }
            if (req.getCardStatusId() != null) {
                // cardStatusId removed from Card entity — skip, use cardStatusCode instead
            } else if (req.getCardStatusCode() != null && !req.getCardStatusCode().isBlank()) {
                predicates.add(cb.equal(root.get("cardStatusCode"), req.getCardStatusCode()));
            }
            if (req.getProductCode() != null && !req.getProductCode().isBlank()) {
                predicates.add(cb.equal(root.get("productCode"), req.getProductCode()));
            }
            if (req.getCardTypeId() != null) {
                // cardTypeId removed from Card entity — skip, use cardTypeCode instead
            } else if (req.getCardTypeCode() != null && !req.getCardTypeCode().isBlank()) {
                predicates.add(cb.equal(root.get("cardTypeCode"), req.getCardTypeCode()));
            }
            if (req.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdOn"), req.getDateFrom().atStartOfDay()));
            }
            if (req.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdOn"), req.getDateTo().atTime(23, 59, 59)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
