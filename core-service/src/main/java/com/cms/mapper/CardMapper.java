package com.cms.mapper;

import com.cms.dal.entity.Card;
import com.cms.dto.response.CardResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardMapper {

    /** Mask PAN for display: show last 4 digits only. */
    public String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return "****";
        return "****" + pan.substring(pan.length() - 4);
    }

    public CardResponse toResponse(Card c) {
        if (c == null) return null;
        CardResponse r = new CardResponse();
        r.setId(c.getCardId());
        r.setCardId(c.getCardId());
        r.setPanMasked(c.getPanLast4() != null && !c.getPanLast4().isBlank()
            ? "****" + c.getPanLast4() : maskPan(c.getPan()));
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
        if (c.getCardStatus() != null) r.setCardStatusName(c.getCardStatus().getCardStatusName());
        if (c.getBranch() != null) r.setBranchName(c.getBranch().getBranchName());
        if (c.getCardType() != null) r.setCardTypeName(c.getCardType().getCardTypeName());
        if (c.getCardProduct() != null) r.setProductName(c.getCardProduct().getProductName());
        return r;
    }

    public List<CardResponse> toResponseList(List<Card> list) {
        if (list == null) return null;
        List<CardResponse> out = new ArrayList<>(list.size());
        for (Card c : list) out.add(toResponse(c));
        return out;
    }
}
