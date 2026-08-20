package com.cms.service.impl;

import com.cms.dal.entity.LimitProfile;
import com.cms.dal.repository.LimitProfileRepository;
import com.cms.dto.request.LimitProfileCreateRequest;
import com.cms.dto.request.LimitProfileUpdateRequest;
import com.cms.dto.response.LimitProfileResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.LimitProfileMapper;
import com.cms.service.LimitProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LimitProfileServiceImpl implements LimitProfileService {

    private final LimitProfileRepository limitProfileRepository;
    private final LimitProfileMapper limitProfileMapper;

    public LimitProfileServiceImpl(LimitProfileRepository limitProfileRepository, LimitProfileMapper limitProfileMapper) {
        this.limitProfileRepository = limitProfileRepository;
        this.limitProfileMapper = limitProfileMapper;
    }

    @Override
    @Transactional
    public LimitProfileResponse create(LimitProfileCreateRequest request) {
        if (request.getProfileCode() != null && limitProfileRepository.findByProfileCode(request.getProfileCode()).isPresent())
            throw new DuplicateResourceException("LimitProfile", request.getProfileCode());
        LimitProfile e = limitProfileMapper.toEntity(request);
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        return limitProfileMapper.toResponse(limitProfileRepository.save(e));
    }

    @Override
    public List<LimitProfileResponse> findAll() {
        return limitProfileMapper.toResponseList(limitProfileRepository.findAll());
    }

    @Override
    public LimitProfileResponse getById(Long id) {
        LimitProfile e = limitProfileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(id)));
        return limitProfileMapper.toResponse(e);
    }

    @Override
    @Transactional
    public LimitProfileResponse update(Long id, LimitProfileUpdateRequest request) {
        LimitProfile e = limitProfileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(id)));
        limitProfileMapper.updateEntity(e, request);
        e.setUpdatedOn(LocalDateTime.now());
        return limitProfileMapper.toResponse(limitProfileRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!limitProfileRepository.existsById(id))
            throw new ResourceNotFoundException("LimitProfile", String.valueOf(id));
        limitProfileRepository.deleteById(id);
    }

    @Override
    public boolean existsByProfileCode(String profileCode) {
        return profileCode != null && limitProfileRepository.findByProfileCode(profileCode).isPresent();
    }
}
