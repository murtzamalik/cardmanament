package com.cms.mapper;

import com.cms.dal.entity.CardLimitActual;
import com.cms.dto.request.CardLimitActualCreateRequest;
import com.cms.dto.response.CardLimitActualResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardLimitActualMapper {

    public CardLimitActualResponse toResponse(CardLimitActual e) {
        if (e == null) return null;
        CardLimitActualResponse r = new CardLimitActualResponse();
        r.setPan(e.getPan());
        r.setChannelCode(e.getChannelCode());
        r.setTranCode(e.getTranCode());
        r.setCycleBeginDate(e.getCycleBeginDate());
        r.setAvailableLimit(e.getAvailableLimit());
        r.setAvailableTranCount(e.getAvailableTranCount());
        r.setCreatedOn(e.getCreatedOn());
        r.setCreatedBy(e.getCreatedBy());
        r.setUpdatedOn(e.getUpdatedOn());
        r.setUpdatedBy(e.getUpdatedBy());
        return r;
    }

    public List<CardLimitActualResponse> toResponseList(List<CardLimitActual> list) {
        if (list == null) return null;
        List<CardLimitActualResponse> out = new ArrayList<>(list.size());
        for (CardLimitActual e : list) out.add(toResponse(e));
        return out;
    }

    public CardLimitActual toEntity(CardLimitActualCreateRequest req) {
        if (req == null) return null;
        CardLimitActual e = new CardLimitActual();
        e.setPan(req.getPan());
        e.setChannelCode(req.getChannelCode().trim().toUpperCase());
        e.setTranCode(CardLimitCustomizedMapper.normalizeTranCode(req.getTranCode()));
        e.setAvailableLimit(req.getAvailableLimit());
        e.setAvailableTranCount(req.getAvailableTranCount());
        e.setCycleBeginDate(req.getCycleBeginDate());
        return e;
    }
}
