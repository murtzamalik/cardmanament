package com.cms.service.impl;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.CardLimitCustomized;
import com.cms.dal.entity.LimitProfile;
import com.cms.dal.repository.CardLimitCustomizedRepository;
import com.cms.dto.request.CardLimitCustomizedCreateRequest;
import com.cms.dto.request.CardLimitCustomizedUpdateRequest;
import com.cms.dto.response.CardLimitCustomizedResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardLimitCustomizedMapper;
import com.cms.service.CardLimitCustomizedService;
import com.cms.service.CardLimitValidationSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardLimitCustomizedServiceImpl implements CardLimitCustomizedService {

    private final CardLimitCustomizedRepository repository;
    private final CardLimitCustomizedMapper mapper;
    private final CardLimitValidationSupport validation;

    public CardLimitCustomizedServiceImpl(CardLimitCustomizedRepository repository,
                                          CardLimitCustomizedMapper mapper,
                                          CardLimitValidationSupport validation) {
        this.repository = repository;
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    @Transactional
    public CardLimitCustomizedResponse create(CardLimitCustomizedCreateRequest request) {
        Card card = validation.requireEligibleCard(request.getPan());
        String tranCode = validation.normalizeTranCode(request.getTranCode());
        request.setTranCode(tranCode);

        if (repository.existsByPanAndTranCode(request.getPan(), tranCode)) {
            throw new DuplicateResourceException("CardLimitCustomized", request.getPan() + "/" + tranCode);
        }

        LimitProfile profile = validation.resolveLimitProfile(card);
        BigDecimal profileMax = validation.dailyAmountForTran(profile, tranCode);
        // Customized can raise ceiling above profile — still cannot be negative
        if (request.getCustomizedLimit() == null || request.getCustomizedLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("customizedLimit must be >= 0");
        }
        // Soft guidance: allow above profile (exception upgrade); block only negative
        if (profileMax != null && request.getCustomizedLimit().compareTo(profileMax) < 0) {
            // Personalized can also be lower than profile; allowed
        }

        CardLimitCustomized e = mapper.toEntity(request);
        e.setTranCode(tranCode);
        LocalDateTime now = LocalDateTime.now();
        if (e.getCycleBeginDate() == null) e.setCycleBeginDate(now);
        String by = validation.auditUser(request.getCreatedBy());
        e.setCreatedOn(now);
        e.setUpdatedOn(now);
        e.setCreatedBy(by);
        e.setUpdatedBy(by);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    public List<CardLimitCustomizedResponse> findByPan(String pan) {
        if (pan == null || pan.isBlank()) {
            throw new BusinessValidationException("pan query param is required");
        }
        return mapper.toResponseList(repository.findByPan(pan.trim()));
    }

    @Override
    public CardLimitCustomizedResponse getById(Long id) {
        return mapper.toResponse(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardLimitCustomized", String.valueOf(id))));
    }

    @Override
    @Transactional
    public CardLimitCustomizedResponse update(Long id, CardLimitCustomizedUpdateRequest request) {
        CardLimitCustomized e = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardLimitCustomized", String.valueOf(id)));
        validation.requireEligibleCard(e.getPan());
        if (request.getCustomizedLimit() != null && request.getCustomizedLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("customizedLimit must be >= 0");
        }
        mapper.updateEntity(e, request);
        e.setUpdatedOn(LocalDateTime.now());
        e.setUpdatedBy(validation.auditUser(request.getUpdatedBy()));
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        CardLimitCustomized e = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardLimitCustomized", String.valueOf(id)));
        e.setIsActive(0);
        e.setUpdatedOn(LocalDateTime.now());
        e.setUpdatedBy("system");
        repository.save(e);
    }
}
