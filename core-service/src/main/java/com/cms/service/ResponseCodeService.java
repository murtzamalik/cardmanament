package com.cms.service;

import com.cms.dto.request.ResponseCodeCreateRequest;
import com.cms.dto.request.ResponseCodeUpdateRequest;
import com.cms.dto.response.ResponseCodeResponse;

import java.util.List;

public interface ResponseCodeService {
    ResponseCodeResponse create(ResponseCodeCreateRequest request);
    List<ResponseCodeResponse> findAll();
    ResponseCodeResponse getById(Long id);
    ResponseCodeResponse update(Long id, ResponseCodeUpdateRequest request);
    void delete(Long id);
}
