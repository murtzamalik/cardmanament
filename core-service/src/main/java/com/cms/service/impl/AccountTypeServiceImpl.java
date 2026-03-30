package com.cms.service.impl;

import com.cms.dal.entity.AccountType;
import com.cms.dal.repository.AccountTypeRepository;
import com.cms.dto.request.AccountTypeCreateRequest;
import com.cms.dto.request.AccountTypeUpdateRequest;
import com.cms.dto.response.AccountTypeResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.AccountTypeMapper;
import com.cms.service.AccountTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeMapper accountTypeMapper;

    public AccountTypeServiceImpl(AccountTypeRepository accountTypeRepository, AccountTypeMapper accountTypeMapper) {
        this.accountTypeRepository = accountTypeRepository;
        this.accountTypeMapper = accountTypeMapper;
    }

    @Override
    @Transactional
    public AccountTypeResponse create(AccountTypeCreateRequest request) {
        if (request.getAcctTypeCode() != null && accountTypeRepository.findByAcctTypeCode(request.getAcctTypeCode()).isPresent())
            throw new DuplicateResourceException("AccountType", request.getAcctTypeCode());
        AccountType e = accountTypeMapper.toEntity(request);
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        return accountTypeMapper.toResponse(accountTypeRepository.save(e));
    }

    @Override
    public List<AccountTypeResponse> findAll() {
        return accountTypeMapper.toResponseList(accountTypeRepository.findAll());
    }

    @Override
    public AccountTypeResponse getById(Long id) {
        AccountType e = accountTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccountType", String.valueOf(id)));
        return accountTypeMapper.toResponse(e);
    }

    @Override
    @Transactional
    public AccountTypeResponse update(Long id, AccountTypeUpdateRequest request) {
        AccountType e = accountTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccountType", String.valueOf(id)));
        if (request.getAcctTypeName() != null) e.setAcctTypeName(request.getAcctTypeName());
        if (request.getIsFrom() != null) e.setIsFrom(request.getIsFrom());
        if (request.getIsTo() != null) e.setIsTo(request.getIsTo());
        if (request.getIsoCode() != null) e.setIsoCode(request.getIsoCode());
        if (request.getGroupId() != null) e.setGroupId(request.getGroupId());
        if (request.getIsLinkingAllowed() != null) e.setIsLinkingAllowed(request.getIsLinkingAllowed());
        e.setUpdatedOn(LocalDateTime.now());
        return accountTypeMapper.toResponse(accountTypeRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!accountTypeRepository.existsById(id))
            throw new ResourceNotFoundException("AccountType", String.valueOf(id));
        accountTypeRepository.deleteById(id);
    }
}
