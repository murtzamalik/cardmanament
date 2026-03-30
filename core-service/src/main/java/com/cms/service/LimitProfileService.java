package com.cms.service;

import com.cms.dto.request.LimitProfileCreateRequest;
import com.cms.dto.request.LimitProfileUpdateRequest;
import com.cms.dto.response.LimitProfileResponse;

import java.util.List;

public interface LimitProfileService {
    LimitProfileResponse create(LimitProfileCreateRequest request);
    List<LimitProfileResponse> findAll();
    LimitProfileResponse getById(Long id);
    LimitProfileResponse update(Long id, LimitProfileUpdateRequest request);
    void delete(Long id);
    boolean existsByProfileCode(String profileCode);
}
