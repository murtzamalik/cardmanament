package com.cms.service;

import com.cms.dto.request.CardProductCreateRequest;
import com.cms.dto.request.CardProductUpdateRequest;
import com.cms.dto.response.CardProductResponse;

import java.util.List;

public interface CardProductService {

    CardProductResponse create(CardProductCreateRequest request);
    List<CardProductResponse> findAll();
    CardProductResponse getById(Long id);
    CardProductResponse update(Long id, CardProductUpdateRequest request);
    void delete(Long id);
}
