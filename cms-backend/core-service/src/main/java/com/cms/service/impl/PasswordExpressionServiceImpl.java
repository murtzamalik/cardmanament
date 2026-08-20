package com.cms.service.impl;

import com.cms.dal.entity.UsmPwdExpression;
import com.cms.dal.repository.UsmPwdExpressionRepository;
import com.cms.dto.request.PasswordExpressionCreateRequest;
import com.cms.dto.request.PasswordExpressionUpdateRequest;
import com.cms.dto.response.PasswordExpressionResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.PasswordExpressionMapper;
import com.cms.service.PasswordExpressionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PasswordExpressionServiceImpl implements PasswordExpressionService {

    private final UsmPwdExpressionRepository usmPwdExpressionRepository;
    private final PasswordExpressionMapper passwordExpressionMapper;

    public PasswordExpressionServiceImpl(UsmPwdExpressionRepository usmPwdExpressionRepository, PasswordExpressionMapper passwordExpressionMapper) {
        this.usmPwdExpressionRepository = usmPwdExpressionRepository;
        this.passwordExpressionMapper = passwordExpressionMapper;
    }

    @Override
    @Transactional
    public PasswordExpressionResponse create(PasswordExpressionCreateRequest request) {
        if (request.getPwdExpId() != null && usmPwdExpressionRepository.findByPwdExpId(request.getPwdExpId()).isPresent())
            throw new DuplicateResourceException("PasswordExpression", request.getPwdExpId());
        UsmPwdExpression e = passwordExpressionMapper.toEntity(request);
        return passwordExpressionMapper.toResponse(usmPwdExpressionRepository.save(e));
    }

    @Override
    public List<PasswordExpressionResponse> findAll() {
        return passwordExpressionMapper.toResponseList(usmPwdExpressionRepository.findAll());
    }

    @Override
    public PasswordExpressionResponse getById(Long id) {
        UsmPwdExpression e = usmPwdExpressionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("PasswordExpression", String.valueOf(id)));
        return passwordExpressionMapper.toResponse(e);
    }

    @Override
    @Transactional
    public PasswordExpressionResponse update(Long id, PasswordExpressionUpdateRequest request) {
        UsmPwdExpression e = usmPwdExpressionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("PasswordExpression", String.valueOf(id)));
        if (request.getPwdExpName() != null) e.setPwdExpName(request.getPwdExpName());
        if (request.getPwdExpression() != null) e.setPwdExpression(request.getPwdExpression());
        if (request.getPwdExpDescription() != null) e.setPwdExpDescription(request.getPwdExpDescription());
        return passwordExpressionMapper.toResponse(usmPwdExpressionRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!usmPwdExpressionRepository.existsById(id))
            throw new ResourceNotFoundException("PasswordExpression", String.valueOf(id));
        usmPwdExpressionRepository.deleteById(id);
    }
}
