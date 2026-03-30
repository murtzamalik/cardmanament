package com.cms.mapper;

import com.cms.dal.entity.UsmPolicy;
import com.cms.dto.request.PolicyCreateRequest;
import com.cms.dto.response.PolicyResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolicyMapper {

    public PolicyResponse toResponse(UsmPolicy e) {
        if (e == null) return null;
        PolicyResponse r = new PolicyResponse();
        r.setId(e.getId());
        r.setPolicyId(e.getPolicyId());
        r.setPolicyName(e.getPolicyName());
        r.setPolicyDescription(e.getPolicyDescription());
        r.setTimeExpression(e.getTimeExpression());
        r.setIsAutoReset(e.getIsAutoReset());
        r.setIsMultiLogin(e.getIsMultiLogin());
        r.setIsDefault(e.getIsDefault());
        r.setPwdExpiryPeriod(e.getPwdExpiryPeriod());
        r.setPwdRetryCount(e.getPwdRetryCount());
        r.setPwdHistoryCount(e.getPwdHistoryCount());
        r.setPwdExpId(e.getPwdExpId());
        r.setCanPwdMatchLogin(e.getCanPwdMatchLogin());
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }

    public List<PolicyResponse> toResponseList(List<UsmPolicy> list) {
        if (list == null) return null;
        List<PolicyResponse> out = new ArrayList<>();
        for (UsmPolicy e : list) out.add(toResponse(e));
        return out;
    }

    public UsmPolicy toEntity(PolicyCreateRequest req) {
        if (req == null) return null;
        UsmPolicy e = new UsmPolicy();
        e.setPolicyId(req.getPolicyId());
        e.setPolicyName(req.getPolicyName());
        e.setPolicyDescription(req.getPolicyDescription());
        e.setTimeExpression(req.getTimeExpression());
        e.setIsAutoReset(req.getIsAutoReset());
        e.setIsMultiLogin(req.getIsMultiLogin());
        e.setIsDefault(req.getIsDefault());
        e.setPwdExpiryPeriod(req.getPwdExpiryPeriod());
        e.setPwdExpiryNotifyPeriod(req.getPwdExpiryNotifyPeriod());
        e.setPwdRetryCount(req.getPwdRetryCount());
        e.setPwdHistoryCount(req.getPwdHistoryCount());
        e.setAccountDisablePeriod(req.getAccountDisablePeriod());
        e.setIsFirstResetRequired(req.getIsFirstResetRequired());
        e.setIsCyclicPwdAllowed(req.getIsCyclicPwdAllowed());
        e.setPwdExpId(req.getPwdExpId());
        e.setPasswordExpression(req.getPasswordExpression());
        e.setCanPwdMatchLogin(req.getCanPwdMatchLogin());
        e.setIsCommonWordAllowed(req.getIsCommonWordAllowed());
        e.setPasswordExpiryCount(req.getPasswordExpiryCount());
        return e;
    }
}
