package com.cms.service;

import com.cms.dto.request.PasswordExpressionCreateRequest;
import com.cms.dto.request.PasswordExpressionUpdateRequest;
import com.cms.dto.response.PasswordExpressionResponse;

import java.util.List;

public interface PasswordExpressionService {
    PasswordExpressionResponse create(PasswordExpressionCreateRequest request);
    List<PasswordExpressionResponse> findAll();
    PasswordExpressionResponse getById(Long id);
    PasswordExpressionResponse update(Long id, PasswordExpressionUpdateRequest request);
    void delete(Long id);
}
