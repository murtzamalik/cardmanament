package com.cms.service;

import com.cms.dto.request.AccountTypeCreateRequest;
import com.cms.dto.request.AccountTypeUpdateRequest;
import com.cms.dto.response.AccountTypeResponse;

import java.util.List;

public interface AccountTypeService {
    AccountTypeResponse create(AccountTypeCreateRequest request);
    List<AccountTypeResponse> findAll();
    AccountTypeResponse getById(Long id);
    AccountTypeResponse update(Long id, AccountTypeUpdateRequest request);
    void delete(Long id);
}
