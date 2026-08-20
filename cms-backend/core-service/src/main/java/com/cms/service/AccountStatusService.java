package com.cms.service;

import com.cms.dto.request.AccountStatusCreateRequest;
import com.cms.dto.request.AccountStatusUpdateRequest;
import com.cms.dto.response.AccountStatusResponse;

import java.util.List;

public interface AccountStatusService {
    AccountStatusResponse create(AccountStatusCreateRequest request);
    List<AccountStatusResponse> findAll();
    AccountStatusResponse getById(Long id);
    AccountStatusResponse update(Long id, AccountStatusUpdateRequest request);
    void delete(Long id);
}
