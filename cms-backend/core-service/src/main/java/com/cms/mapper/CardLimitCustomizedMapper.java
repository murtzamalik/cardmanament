package com.cms.mapper;

import com.cms.dal.entity.CardLimitCustomized;
import com.cms.dto.request.CardLimitCustomizedCreateRequest;
import com.cms.dto.request.CardLimitCustomizedUpdateRequest;
import com.cms.dto.response.CardLimitCustomizedResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardLimitCustomizedMapper {

    public CardLimitCustomizedResponse toResponse(CardLimitCustomized e) {
        if (e == null) return null;
        CardLimitCustomizedResponse r = new CardLimitCustomizedResponse();
        r.setId(e.getId());
        r.setPan(e.getPan());
        r.setTranCode(e.getTranCode());
        r.setCustomizedLimit(e.getCustomizedLimit());
        r.setCustomizedTranCount(e.getCustomizedTranCount());
        r.setCycleBeginDate(e.getCycleBeginDate());
        r.setIsActive(e.getIsActive() != null && e.getIsActive() == 1);
        r.setCreatedOn(e.getCreatedOn());
        r.setCreatedBy(e.getCreatedBy());
        r.setUpdatedOn(e.getUpdatedOn());
        r.setUpdatedBy(e.getUpdatedBy());
        return r;
    }

    public List<CardLimitCustomizedResponse> toResponseList(List<CardLimitCustomized> list) {
        if (list == null) return null;
        List<CardLimitCustomizedResponse> out = new ArrayList<>(list.size());
        for (CardLimitCustomized e : list) out.add(toResponse(e));
        return out;
    }

    public CardLimitCustomized toEntity(CardLimitCustomizedCreateRequest req) {
        if (req == null) return null;
        CardLimitCustomized e = new CardLimitCustomized();
        e.setPan(req.getPan());
        e.setTranCode(normalizeTranCode(req.getTranCode()));
        e.setCustomizedLimit(req.getCustomizedLimit());
        e.setCustomizedTranCount(req.getCustomizedTranCount());
        e.setCycleBeginDate(req.getCycleBeginDate());
        e.setIsActive(req.getIsActive() == null || Boolean.TRUE.equals(req.getIsActive()) ? 1 : 0);
        return e;
    }

    public void updateEntity(CardLimitCustomized e, CardLimitCustomizedUpdateRequest req) {
        if (e == null || req == null) return;
        if (req.getCustomizedLimit() != null) e.setCustomizedLimit(req.getCustomizedLimit());
        if (req.getCustomizedTranCount() != null) e.setCustomizedTranCount(req.getCustomizedTranCount());
        if (req.getCycleBeginDate() != null) e.setCycleBeginDate(req.getCycleBeginDate());
        if (req.getIsActive() != null) e.setIsActive(Boolean.TRUE.equals(req.getIsActive()) ? 1 : 0);
    }

    public static String normalizeTranCode(String tranCode) {
        if (tranCode == null) return null;
        return tranCode.trim().toUpperCase();
    }
}
