package com.cms.service.impl;

import com.cms.dal.entity.Branch;
import com.cms.dal.repository.BranchRepository;
import com.cms.dto.request.BranchCreateRequest;
import com.cms.dto.request.BranchUpdateRequest;
import com.cms.dto.response.BranchResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.BranchMapper;
import com.cms.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public BranchServiceImpl(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }

    @Override
    @Transactional
    public BranchResponse create(BranchCreateRequest request) {
        if (request.getBranchCode() != null && branchRepository.findByBranchCode(request.getBranchCode()).isPresent())
            throw new DuplicateResourceException("Branch", request.getBranchCode());
        Branch b = branchMapper.toEntity(request);
        b.setCreatedOn(LocalDateTime.now());
        b.setUpdatedOn(LocalDateTime.now());
        return branchMapper.toResponse(branchRepository.save(b));
    }

    @Override
    public List<BranchResponse> findAll() {
        return branchMapper.toResponseList(branchRepository.findAll());
    }

    @Override
    public BranchResponse getById(Long id) {
        Branch b = branchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Branch", String.valueOf(id)));
        return branchMapper.toResponse(b);
    }

    @Override
    @Transactional
    public BranchResponse update(Long id, BranchUpdateRequest request) {
        Branch b = branchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Branch", String.valueOf(id)));
        if (request.getBranchName() != null) b.setBranchName(request.getBranchName());
        if (request.getCityCode() != null) b.setCityCode(request.getCityCode());
        if (request.getCountryCode() != null) b.setCountryCode(request.getCountryCode());
        if (request.getSwiftCode() != null) b.setSwiftCode(request.getSwiftCode());
        b.setUpdatedOn(LocalDateTime.now());
        return branchMapper.toResponse(branchRepository.save(b));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!branchRepository.existsById(id))
            throw new ResourceNotFoundException("Branch", String.valueOf(id));
        branchRepository.deleteById(id);
    }
}
