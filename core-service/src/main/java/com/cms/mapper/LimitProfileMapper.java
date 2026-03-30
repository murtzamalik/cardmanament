package com.cms.mapper;

import com.cms.dal.entity.LimitProfile;
import com.cms.dto.request.LimitProfileCreateRequest;
import com.cms.dto.request.LimitProfileUpdateRequest;
import com.cms.dto.response.LimitProfileResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LimitProfileMapper {

    public LimitProfileResponse toResponse(LimitProfile e) {
        if (e == null) return null;
        LimitProfileResponse r = new LimitProfileResponse();
        r.setId(e.getId());
        r.setProfileCode(e.getProfileCode());
        r.setProfileName(e.getProfileName());
        r.setCurrencyCode(e.getCurrencyCode());
        r.setAtmDailyAmount(e.getAtmDailyAmount());
        r.setAtmMonthlyAmount(e.getAtmMonthlyAmount());
        r.setAtmYearlyAmount(e.getAtmYearlyAmount());
        r.setPosDailyAmount(e.getPosDailyAmount());
        r.setPosMonthlyAmount(e.getPosMonthlyAmount());
        r.setPosYearlyAmount(e.getPosYearlyAmount());
        r.setEcommerceDailyAmount(e.getEcommerceDailyAmount());
        r.setEcommerceMonthlyAmount(e.getEcommerceMonthlyAmount());
        r.setEcommerceYearlyAmount(e.getEcommerceYearlyAmount());
        r.setActive(e.getIsActive() != null && e.getIsActive() == 1);
        return r;
    }

    public List<LimitProfileResponse> toResponseList(List<LimitProfile> list) {
        if (list == null) return null;
        List<LimitProfileResponse> out = new ArrayList<>(list.size());
        for (LimitProfile e : list) out.add(toResponse(e));
        return out;
    }

    public LimitProfile toEntity(LimitProfileCreateRequest req) {
        if (req == null) return null;
        LimitProfile e = new LimitProfile();
        e.setProfileCode(req.getProfileCode());
        e.setProfileName(req.getProfileName());
        e.setCurrencyCode(req.getCurrencyCode());
        e.setAtmDailyAmount(req.getAtmDailyAmount());
        e.setAtmMonthlyAmount(req.getAtmMonthlyAmount());
        e.setAtmYearlyAmount(req.getAtmYearlyAmount());
        e.setPosDailyAmount(req.getPosDailyAmount());
        e.setPosMonthlyAmount(req.getPosMonthlyAmount());
        e.setPosYearlyAmount(req.getPosYearlyAmount());
        e.setEcommerceDailyAmount(req.getEcommerceDailyAmount());
        e.setEcommerceMonthlyAmount(req.getEcommerceMonthlyAmount());
        e.setEcommerceYearlyAmount(req.getEcommerceYearlyAmount());
        e.setIsActive(Boolean.TRUE.equals(req.getActive()) ? 1 : 0);
        return e;
    }

    public void updateEntity(LimitProfile e, LimitProfileUpdateRequest req) {
        if (e == null || req == null) return;
        if (req.getProfileName() != null) e.setProfileName(req.getProfileName());
        if (req.getCurrencyCode() != null) e.setCurrencyCode(req.getCurrencyCode());
        if (req.getAtmDailyAmount() != null) e.setAtmDailyAmount(req.getAtmDailyAmount());
        if (req.getAtmMonthlyAmount() != null) e.setAtmMonthlyAmount(req.getAtmMonthlyAmount());
        if (req.getAtmYearlyAmount() != null) e.setAtmYearlyAmount(req.getAtmYearlyAmount());
        if (req.getPosDailyAmount() != null) e.setPosDailyAmount(req.getPosDailyAmount());
        if (req.getPosMonthlyAmount() != null) e.setPosMonthlyAmount(req.getPosMonthlyAmount());
        if (req.getPosYearlyAmount() != null) e.setPosYearlyAmount(req.getPosYearlyAmount());
        if (req.getEcommerceDailyAmount() != null) e.setEcommerceDailyAmount(req.getEcommerceDailyAmount());
        if (req.getEcommerceMonthlyAmount() != null) e.setEcommerceMonthlyAmount(req.getEcommerceMonthlyAmount());
        if (req.getEcommerceYearlyAmount() != null) e.setEcommerceYearlyAmount(req.getEcommerceYearlyAmount());
        if (req.getActive() != null) e.setIsActive(Boolean.TRUE.equals(req.getActive()) ? 1 : 0);
    }
}
