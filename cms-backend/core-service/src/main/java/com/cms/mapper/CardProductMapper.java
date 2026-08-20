package com.cms.mapper;

import com.cms.dal.entity.CardProduct;
import com.cms.dto.request.CardProductCreateRequest;
import com.cms.dto.response.CardProductResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardProductMapper {

    public CardProductResponse toResponse(CardProduct e) {
        if (e == null) return null;
        CardProductResponse r = new CardProductResponse();
        r.setId(e.getId());
        r.setProductCode(e.getProductCode());
        r.setProductName(e.getProductName());
        r.setBin(e.getBin());
        r.setIsActive(e.getIsActive() != null && e.getIsActive() == 1);
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }

    public List<CardProductResponse> toResponseList(List<CardProduct> list) {
        if (list == null) return null;
        List<CardProductResponse> out = new ArrayList<>();
        for (CardProduct e : list) out.add(toResponse(e));
        return out;
    }

    public CardProduct toEntity(CardProductCreateRequest req) {
        if (req == null) return null;
        CardProduct e = new CardProduct();
        e.setProductCode(req.getProductCode());
        e.setProductName(req.getProductName());
        e.setBin(req.getBin());
        e.setIsActive(Boolean.TRUE.equals(req.getIsActive()) ? 1 : 0);
        return e;
    }
}
