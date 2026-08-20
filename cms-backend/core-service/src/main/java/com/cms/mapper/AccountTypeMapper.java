package com.cms.mapper;

import com.cms.dal.entity.AccountType;
import com.cms.dto.request.AccountTypeCreateRequest;
import com.cms.dto.response.AccountTypeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountTypeMapper {

    public AccountTypeResponse toResponse(AccountType e) {
        if (e == null) return null;
        AccountTypeResponse r = new AccountTypeResponse();
        r.setId(e.getId());
        r.setAcctTypeCode(e.getAcctTypeCode());
        r.setAcctTypeName(e.getAcctTypeName());
        r.setIsFrom(e.getIsFrom());
        r.setIsTo(e.getIsTo());
        r.setIsoCode(e.getIsoCode());
        r.setGroupId(e.getGroupId());
        r.setIsLinkingAllowed(e.getIsLinkingAllowed());
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }

    public List<AccountTypeResponse> toResponseList(List<AccountType> list) {
        if (list == null) return null;
        List<AccountTypeResponse> out = new ArrayList<>();
        for (AccountType e : list) out.add(toResponse(e));
        return out;
    }

    public AccountType toEntity(AccountTypeCreateRequest req) {
        if (req == null) return null;
        AccountType e = new AccountType();
        e.setAcctTypeCode(req.getAcctTypeCode());
        e.setAcctTypeName(req.getAcctTypeName());
        e.setIsFrom(req.getIsFrom());
        e.setIsTo(req.getIsTo());
        e.setIsoCode(req.getIsoCode());
        e.setGroupId(req.getGroupId());
        e.setIsLinkingAllowed(req.getIsLinkingAllowed());
        return e;
    }
}
