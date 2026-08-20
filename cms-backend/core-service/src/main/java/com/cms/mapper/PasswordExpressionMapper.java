package com.cms.mapper;

import com.cms.dal.entity.UsmPwdExpression;
import com.cms.dto.request.PasswordExpressionCreateRequest;
import com.cms.dto.response.PasswordExpressionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PasswordExpressionMapper {

    public PasswordExpressionResponse toResponse(UsmPwdExpression e) {
        if (e == null) return null;
        PasswordExpressionResponse r = new PasswordExpressionResponse();
        r.setId(e.getId());
        r.setPwdExpId(e.getPwdExpId());
        r.setPwdExpName(e.getPwdExpName());
        r.setPwdExpression(e.getPwdExpression());
        r.setPwdExpDescription(e.getPwdExpDescription());
        return r;
    }

    public List<PasswordExpressionResponse> toResponseList(List<UsmPwdExpression> list) {
        if (list == null) return null;
        List<PasswordExpressionResponse> out = new ArrayList<>();
        for (UsmPwdExpression e : list) out.add(toResponse(e));
        return out;
    }

    public UsmPwdExpression toEntity(PasswordExpressionCreateRequest req) {
        if (req == null) return null;
        UsmPwdExpression e = new UsmPwdExpression();
        e.setPwdExpId(req.getPwdExpId());
        e.setPwdExpName(req.getPwdExpName());
        e.setPwdExpression(req.getPwdExpression());
        e.setPwdExpDescription(req.getPwdExpDescription());
        return e;
    }
}
