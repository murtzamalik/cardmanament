package com.cms.mapper;

import com.cms.dal.entity.AccountStatus;
import com.cms.dto.request.AccountStatusCreateRequest;
import com.cms.dto.response.AccountStatusResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountStatusMapper {

    public AccountStatusResponse toResponse(AccountStatus e) {
        if (e == null) return null;
        AccountStatusResponse r = new AccountStatusResponse();
        r.setId(e.getId());
        r.setAcctStatusCode(e.getAcctStatusCode());
        r.setAcctStatusName(e.getAcctStatusName());
        r.setDescription(e.getDescription());
        r.setIsTranAllowed(e.getIsTranAllowed());
        r.setIsLinkingAllowed(e.getIsLinkingAllowed());
        r.setIsoCode(e.getIsoCode());
        r.setIsActive(e.getIsActive());
        r.setGroupId(e.getGroupId());
        r.setMappingId(e.getMappingId());
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }

    public List<AccountStatusResponse> toResponseList(List<AccountStatus> list) {
        if (list == null) return null;
        List<AccountStatusResponse> out = new ArrayList<>();
        for (AccountStatus e : list) out.add(toResponse(e));
        return out;
    }

    public AccountStatus toEntity(AccountStatusCreateRequest req) {
        if (req == null) return null;
        AccountStatus e = new AccountStatus();
        e.setAcctStatusCode(req.getAcctStatusCode());
        e.setAcctStatusName(req.getAcctStatusName());
        e.setDescription(req.getDescription());
        e.setIsTranAllowed(req.getIsTranAllowed());
        e.setIsLinkingAllowed(req.getIsLinkingAllowed());
        e.setIsoCode(req.getIsoCode());
        e.setGroupId(req.getGroupId());
        e.setMappingId(req.getMappingId());
        return e;
    }
}
