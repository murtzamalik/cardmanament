package com.cms.mapper;

import com.cms.dal.entity.UsmPermission;
import com.cms.dto.request.PermissionCreateRequest;
import com.cms.dto.response.PermissionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(UsmPermission p) {
        if (p == null) return null;
        PermissionResponse r = new PermissionResponse();
        r.setId(p.getId());
        r.setPermissionId(p.getPermissionId());
        r.setPerParentId(p.getPerParentId());
        r.setPermissionName(p.getPermissionName());
        r.setPermissionType(p.getPermissionType());
        return r;
    }

    public List<PermissionResponse> toResponseList(List<UsmPermission> list) {
        if (list == null) return null;
        List<PermissionResponse> out = new ArrayList<>();
        for (UsmPermission e : list) out.add(toResponse(e));
        return out;
    }

    public UsmPermission toEntity(PermissionCreateRequest req) {
        if (req == null) return null;
        UsmPermission p = new UsmPermission();
        p.setPermissionId(req.getPermissionId());
        p.setPerParentId(req.getPerParentId());
        p.setPermissionName(req.getPermissionName());
        p.setPermissionType(req.getPermissionType());
        return p;
    }
}
