package com.cms.service;

import com.cms.dto.request.BranchCreateRequest;
import com.cms.dto.request.BranchUpdateRequest;
import com.cms.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    BranchResponse create(BranchCreateRequest request);
    List<BranchResponse> findAll();
    BranchResponse getById(Long id);
    BranchResponse update(Long id, BranchUpdateRequest request);
    void delete(Long id);
}
