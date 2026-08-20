package com.cms.service.impl;

import com.cms.dal.entity.AccountStatus;
import com.cms.dal.repository.AccountStatusRepository;
import com.cms.dto.request.AccountStatusCreateRequest;
import com.cms.dto.request.AccountStatusUpdateRequest;
import com.cms.dto.response.AccountStatusResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.AccountStatusMapper;
import com.cms.service.AccountStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountStatusServiceImpl implements AccountStatusService {

    private final AccountStatusRepository accountStatusRepository;
    private final AccountStatusMapper accountStatusMapper;

    public AccountStatusServiceImpl(AccountStatusRepository accountStatusRepository, AccountStatusMapper accountStatusMapper) {
        this.accountStatusRepository = accountStatusRepository;
        this.accountStatusMapper = accountStatusMapper;
    }

    @Override
    @Transactional
    public AccountStatusResponse create(AccountStatusCreateRequest request) {
        if (request.getAcctStatusCode() != null && accountStatusRepository.findByAcctStatusCode(request.getAcctStatusCode()).isPresent())
            throw new DuplicateResourceException("AccountStatus", request.getAcctStatusCode());
        AccountStatus e = accountStatusMapper.toEntity(request);
        e.setIsActive(BigDecimal.ONE);
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        return accountStatusMapper.toResponse(accountStatusRepository.save(e));
    }

    @Override
    public List<AccountStatusResponse> findAll() {
        return accountStatusMapper.toResponseList(accountStatusRepository.findAll());
    }

    @Override
    public AccountStatusResponse getById(Long id) {
        AccountStatus e = accountStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccountStatus", String.valueOf(id)));
        return accountStatusMapper.toResponse(e);
    }

    @Override
    @Transactional
    public AccountStatusResponse update(Long id, AccountStatusUpdateRequest request) {
        AccountStatus e = accountStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccountStatus", String.valueOf(id)));
        if (request.getAcctStatusName() != null) e.setAcctStatusName(request.getAcctStatusName());
        if (request.getDescription() != null) e.setDescription(request.getDescription());
        if (request.getIsTranAllowed() != null) e.setIsTranAllowed(request.getIsTranAllowed());
        if (request.getIsLinkingAllowed() != null) e.setIsLinkingAllowed(request.getIsLinkingAllowed());
        if (request.getIsoCode() != null) e.setIsoCode(request.getIsoCode());
        if (request.getIsActive() != null) e.setIsActive(request.getIsActive());
        if (request.getGroupId() != null) e.setGroupId(request.getGroupId());
        if (request.getMappingId() != null) e.setMappingId(request.getMappingId());
        e.setUpdatedOn(LocalDateTime.now());
        return accountStatusMapper.toResponse(accountStatusRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!accountStatusRepository.existsById(id))
            throw new ResourceNotFoundException("AccountStatus", String.valueOf(id));
        accountStatusRepository.deleteById(id);
    }
}
