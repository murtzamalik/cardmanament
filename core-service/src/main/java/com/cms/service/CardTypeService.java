package com.cms.service;

import com.cms.dto.request.CardTypeCreateRequest;
import com.cms.dto.request.CardTypeUpdateRequest;
import com.cms.dto.response.CardTypeResponse;

import java.util.List;

public interface CardTypeService {

    CardTypeResponse create(CardTypeCreateRequest request);
    List<CardTypeResponse> findAll();
    CardTypeResponse getById(Long id);
    CardTypeResponse update(Long id, CardTypeUpdateRequest request);
    void delete(Long id);
}
