package com.cms.service;

import com.cms.dto.request.CardLimitCustomizedCreateRequest;
import com.cms.dto.request.CardLimitCustomizedUpdateRequest;
import com.cms.dto.response.CardLimitCustomizedResponse;

import java.util.List;

public interface CardLimitCustomizedService {

    CardLimitCustomizedResponse create(CardLimitCustomizedCreateRequest request);

    List<CardLimitCustomizedResponse> findByPan(String pan);

    CardLimitCustomizedResponse getById(Long id);

    CardLimitCustomizedResponse update(Long id, CardLimitCustomizedUpdateRequest request);

    void deactivate(Long id);
}
