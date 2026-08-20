package com.cms.mapper;

import com.cms.dal.entity.UsmGroup;
import com.cms.dto.request.RoleCreateRequest;
import com.cms.dto.response.RoleResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoleMapper {

    public RoleResponse toResponse(UsmGroup g) {
        if (g == null) return null;
        RoleResponse r = new RoleResponse();
        r.setId(g.getId());
        r.setGroupId(g.getGroupId());
        r.setGroupName(g.getGroupName());
        r.setActive(g.getIsActive() != null && g.getIsActive().intValue() == 1);
        return r;
    }

    public List<RoleResponse> toResponseList(List<UsmGroup> list) {
        if (list == null) return null;
        List<RoleResponse> out = new ArrayList<>();
        for (UsmGroup e : list) out.add(toResponse(e));
        return out;
    }

    public UsmGroup toEntity(RoleCreateRequest req) {
        if (req == null) return null;
        UsmGroup g = new UsmGroup();
        g.setGroupId(req.getGroupId());
        g.setGroupName(req.getGroupName());
        g.setIsActive(req.getActive() != null && req.getActive() ? BigDecimal.ONE : BigDecimal.ZERO);
        return g;
    }
}
