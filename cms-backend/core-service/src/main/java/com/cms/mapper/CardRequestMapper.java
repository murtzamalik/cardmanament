package com.cms.mapper;

import com.cms.dal.entity.CardRequest;
import com.cms.dto.response.CardRequestResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardRequestMapper {

    public CardRequestResponse toResponse(CardRequest r) {
        if (r == null) return null;
        CardRequestResponse resp = new CardRequestResponse();
        resp.setId(r.getRequestId());
        resp.setRequestId(r.getRequestId());
        resp.setRelationshipNum(r.getRelationshipNum());
        resp.setAccountNum(r.getAccountNum());
        resp.setCardTitle(r.getCardTitle());
        resp.setCardTypeCode(r.getCardTypeCode());
        resp.setProductCode(r.getProductCode());
        resp.setBranchCode(r.getBranchCode());
        resp.setSupplementaryCount(r.getSupplementaryCount());
        resp.setIsProcessed(r.getIsProcessed());
        resp.setProgressFlag(r.getProgressFlag());
        resp.setRequestTypeId(r.getRequestTypeId());
        resp.setSourceCardId(r.getSourceCardId());
        resp.setCreatedOn(r.getCreatedOn());
        resp.setCreatedBy(r.getCreatedBy());
        // Relationships removed from CardRequest entity — names not available directly
        return resp;
    }

    public List<CardRequestResponse> toResponseList(List<CardRequest> list) {
        if (list == null) return null;
        List<CardRequestResponse> out = new ArrayList<>(list.size());
        for (CardRequest r : list) out.add(toResponse(r));
        return out;
    }
}
