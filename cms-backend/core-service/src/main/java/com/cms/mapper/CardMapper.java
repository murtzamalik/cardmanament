package com.cms.mapper;

import com.cms.dal.entity.Card;
import com.cms.dto.response.CardResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardMapper {

    /** Mask PAN for display: show first 6 and last 4 digits. */
    public String maskPan(String pan) {
        if (pan == null || pan.isBlank()) return "****";
        if (pan.contains("*")) return pan;
        if (pan.length() <= 10) return "****";
        return pan.substring(0, 6)
                + "*".repeat(pan.length() - 10)
                + pan.substring(pan.length() - 4);
    }

    public CardResponse toResponse(Card c) {
        if (c == null) return null;
        CardResponse r = new CardResponse();
        r.setId(c.getCardId());
        r.setCardId(c.getCardId());
        String panForMask = c.getPan();
        if (panForMask != null && panForMask.contains("*")) {
            r.setPanMasked(panForMask);
        } else if ((panForMask == null || panForMask.isBlank()) && c.getPanLast4() != null && !c.getPanLast4().isBlank()) {
            r.setPanMasked("************" + c.getPanLast4());
        } else {
            r.setPanMasked(maskPan(panForMask));
        }
        r.setRelationshipNum(c.getRelationshipNum());
        r.setCardTitle(c.getCardTitle());
        r.setExpiryDate(c.getExpiryDate());
        r.setCardTypeCode(c.getCardTypeCode());
        r.setCardStatusCode(c.getCardStatusCode());
        r.setProductCode(c.getProductCode());
        r.setBranchCode(c.getBranchCode());
        r.setLimitProfile(c.getLimitProfile());
        r.setActivationDate(c.getActivationDate());
        r.setIssuedDate(c.getIssuedDate());
        r.setCreatedOn(c.getCreatedOn());
        r.setCreatedBy(c.getCreatedBy());
        r.setExportFilePath(c.getExportFilePath());
        // Relationships removed — names resolved from codes by fillNamesFromCodes in CardServiceImpl
        return r;
    }

    public List<CardResponse> toResponseList(List<Card> list) {
        if (list == null) return null;
        List<CardResponse> out = new ArrayList<>(list.size());
        for (Card c : list) out.add(toResponse(c));
        return out;
    }
}
