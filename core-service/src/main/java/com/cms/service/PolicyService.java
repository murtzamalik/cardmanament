package com.cms.service;

import com.cms.dto.request.PolicyCreateRequest;
import com.cms.dto.request.PolicyUpdateRequest;
import com.cms.dto.response.PolicyResponse;

import java.util.List;

public interface PolicyService {
    PolicyResponse create(PolicyCreateRequest request);
    List<PolicyResponse> findAll();
    PolicyResponse getById(Long id);
    PolicyResponse update(Long id, PolicyUpdateRequest request);
    void delete(Long id);
}
