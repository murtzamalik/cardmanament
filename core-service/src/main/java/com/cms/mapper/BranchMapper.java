package com.cms.mapper;

import com.cms.dal.entity.Branch;
import com.cms.dto.request.BranchCreateRequest;
import com.cms.dto.response.BranchResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BranchMapper {

    public BranchResponse toResponse(Branch b) {
        if (b == null) return null;
        BranchResponse r = new BranchResponse();
        r.setId(b.getId());
        r.setBranchCode(b.getBranchCode());
        r.setBranchName(b.getBranchName());
        r.setCityCode(b.getCityCode());
        r.setCountryCode(b.getCountryCode());
        r.setSwiftCode(b.getSwiftCode());
        return r;
    }

    public List<BranchResponse> toResponseList(List<Branch> list) {
        if (list == null) return null;
        List<BranchResponse> out = new ArrayList<>(list.size());
        for (Branch b : list) out.add(toResponse(b));
        return out;
    }

    public Branch toEntity(BranchCreateRequest req) {
        if (req == null) return null;
        Branch b = new Branch();
        b.setBranchCode(req.getBranchCode());
        b.setBranchName(req.getBranchName());
        b.setCityCode(req.getCityCode());
        b.setCountryCode(req.getCountryCode());
        b.setSwiftCode(req.getSwiftCode());
        return b;
    }
}
