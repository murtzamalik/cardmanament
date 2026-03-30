package com.cms.service.impl;

import com.cms.dal.entity.ResponseCode;
import com.cms.dal.repository.ResponseCodeRepository;
import com.cms.dto.request.ResponseCodeCreateRequest;
import com.cms.dto.request.ResponseCodeUpdateRequest;
import com.cms.dto.response.ResponseCodeResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.ResponseCodeMapper;
import com.cms.service.ResponseCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponseCodeServiceImpl implements ResponseCodeService {

    private final ResponseCodeRepository responseCodeRepository;
    private final ResponseCodeMapper responseCodeMapper;

    public ResponseCodeServiceImpl(ResponseCodeRepository responseCodeRepository, ResponseCodeMapper responseCodeMapper) {
        this.responseCodeRepository = responseCodeRepository;
        this.responseCodeMapper = responseCodeMapper;
    }

    @Override
    @Transactional
    public ResponseCodeResponse create(ResponseCodeCreateRequest request) {
        if (request.getCode() != null && responseCodeRepository.findByCode(request.getCode()).isPresent())
            throw new DuplicateResourceException("ResponseCode", request.getCode());
        ResponseCode e = responseCodeMapper.toEntity(request);
        return responseCodeMapper.toResponse(responseCodeRepository.save(e));
    }

    @Override
    public List<ResponseCodeResponse> findAll() {
        return responseCodeMapper.toResponseList(responseCodeRepository.findAll());
    }

    @Override
    public ResponseCodeResponse getById(Long id) {
        ResponseCode e = responseCodeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ResponseCode", String.valueOf(id)));
        return responseCodeMapper.toResponse(e);
    }

    @Override
    @Transactional
    public ResponseCodeResponse update(Long id, ResponseCodeUpdateRequest request) {
        ResponseCode e = responseCodeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ResponseCode", String.valueOf(id)));
        if (request.getShortDescription() != null) e.setShortDescription(request.getShortDescription());
        if (request.getFullDescription() != null) e.setFullDescription(request.getFullDescription());
        if (request.getAlertType() != null) e.setAlertType(request.getAlertType());
        if (request.getHttpResponseCode() != null) e.setHttpResponseCode(request.getHttpResponseCode());
        return responseCodeMapper.toResponse(responseCodeRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!responseCodeRepository.existsById(id))
            throw new ResourceNotFoundException("ResponseCode", String.valueOf(id));
        responseCodeRepository.deleteById(id);
    }
}
