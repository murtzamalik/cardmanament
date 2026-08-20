package com.cms.service.impl;

import com.cms.dal.entity.UsmPolicy;
import com.cms.dal.repository.UsmPolicyRepository;
import com.cms.dto.request.PolicyCreateRequest;
import com.cms.dto.request.PolicyUpdateRequest;
import com.cms.dto.response.PolicyResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.PolicyMapper;
import com.cms.service.PolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final UsmPolicyRepository usmPolicyRepository;
    private final PolicyMapper policyMapper;

    public PolicyServiceImpl(UsmPolicyRepository usmPolicyRepository, PolicyMapper policyMapper) {
        this.usmPolicyRepository = usmPolicyRepository;
        this.policyMapper = policyMapper;
    }

    @Override
    @Transactional
    public PolicyResponse create(PolicyCreateRequest request) {
        if (request.getPolicyId() != null && usmPolicyRepository.findByPolicyId(request.getPolicyId()).isPresent())
            throw new DuplicateResourceException("Policy", request.getPolicyId());
        UsmPolicy e = policyMapper.toEntity(request);
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        return policyMapper.toResponse(usmPolicyRepository.save(e));
    }

    @Override
    public List<PolicyResponse> findAll() {
        return policyMapper.toResponseList(usmPolicyRepository.findAll());
    }

    @Override
    public PolicyResponse getById(Long id) {
        UsmPolicy e = usmPolicyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Policy", String.valueOf(id)));
        return policyMapper.toResponse(e);
    }

    @Override
    @Transactional
    public PolicyResponse update(Long id, PolicyUpdateRequest request) {
        UsmPolicy e = usmPolicyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Policy", String.valueOf(id)));
        if (request.getPolicyName() != null) e.setPolicyName(request.getPolicyName());
        if (request.getPolicyDescription() != null) e.setPolicyDescription(request.getPolicyDescription());
        if (request.getTimeExpression() != null) e.setTimeExpression(request.getTimeExpression());
        if (request.getIsAutoReset() != null) e.setIsAutoReset(request.getIsAutoReset());
        if (request.getIsMultiLogin() != null) e.setIsMultiLogin(request.getIsMultiLogin());
        if (request.getIsDefault() != null) e.setIsDefault(request.getIsDefault());
        if (request.getPwdExpiryPeriod() != null) e.setPwdExpiryPeriod(request.getPwdExpiryPeriod());
        if (request.getPwdRetryCount() != null) e.setPwdRetryCount(request.getPwdRetryCount());
        if (request.getPwdHistoryCount() != null) e.setPwdHistoryCount(request.getPwdHistoryCount());
        if (request.getPwdExpId() != null) e.setPwdExpId(request.getPwdExpId());
        if (request.getPasswordExpression() != null) e.setPasswordExpression(request.getPasswordExpression());
        if (request.getCanPwdMatchLogin() != null) e.setCanPwdMatchLogin(request.getCanPwdMatchLogin());
        if (request.getIsCommonWordAllowed() != null) e.setIsCommonWordAllowed(request.getIsCommonWordAllowed());
        if (request.getPasswordExpiryCount() != null) e.setPasswordExpiryCount(request.getPasswordExpiryCount());
        e.setUpdatedOn(LocalDateTime.now());
        return policyMapper.toResponse(usmPolicyRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!usmPolicyRepository.existsById(id))
            throw new ResourceNotFoundException("Policy", String.valueOf(id));
        usmPolicyRepository.deleteById(id);
    }
}
