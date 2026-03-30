package com.cms.mapper;

import com.cms.dal.entity.CardType;
import com.cms.dto.request.CardTypeCreateRequest;
import com.cms.dto.response.CardTypeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardTypeMapper {

    public CardTypeResponse toResponse(CardType e) {
        if (e == null) return null;
        CardTypeResponse r = new CardTypeResponse();
        r.setId(e.getId());
        r.setCardTypeCode(e.getCardTypeCode());
        r.setCardTypeName(e.getCardTypeName());
        r.setProductId(e.getProductId());
        r.setProductCode(e.getProductCode());
        r.setIsActive(e.getIsActive() != null && e.getIsActive() == 1);
        r.setSupplementaryAllowed(e.getSupplementaryAllowed());
        r.setIsSuppType(e.getIsSuppType());
        r.setSuppTypeCode(e.getSuppTypeCode());
        r.setPanLength(e.getPanLength());
        r.setBin(e.getBin());
        r.setExpPeriod(e.getExpPeriod());
        r.setPanSequenceName(e.getPanSequenceName());
        r.setPanSequenceLength(e.getPanSequenceLength());
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }

    public List<CardTypeResponse> toResponseList(List<CardType> list) {
        if (list == null) return null;
        List<CardTypeResponse> out = new ArrayList<>();
        for (CardType e : list) out.add(toResponse(e));
        return out;
    }

    public CardType toEntity(CardTypeCreateRequest req) {
        if (req == null) return null;
        CardType e = new CardType();
        e.setCardTypeCode(req.getCardTypeCode());
        e.setCardTypeName(req.getCardTypeName());
        e.setProductCode(req.getProductCode());
        e.setIsActive(Boolean.TRUE.equals(req.getIsActive()) ? 1 : 0);
        e.setSupplementaryAllowed(req.getSupplementaryAllowed());
        e.setIsSuppType(req.getIsSuppType());
        e.setSuppTypeCode(req.getSuppTypeCode());
        e.setPanLength(req.getPanLength());
        e.setBin(req.getBin());
        e.setExpPeriod(req.getExpPeriod());
        e.setPanSequenceName(req.getPanSequenceName());
        e.setPanSequenceLength(req.getPanSequenceLength());
        return e;
    }
}
