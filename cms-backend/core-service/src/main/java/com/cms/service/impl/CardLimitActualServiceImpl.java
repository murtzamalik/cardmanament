package com.cms.service.impl;

import com.cms.dal.entity.Card;
import com.cms.dal.entity.CardLimitActual;
import com.cms.dal.repository.CardLimitActualRepository;
import com.cms.dto.request.CardLimitActualCreateRequest;
import com.cms.dto.request.CardLimitActualUpdateRequest;
import com.cms.dto.response.CardLimitActualResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardLimitActualMapper;
import com.cms.service.CardLimitActualService;
import com.cms.service.CardLimitValidationSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CardLimitActualServiceImpl implements CardLimitActualService {

    private final CardLimitActualRepository repository;
    private final CardLimitActualMapper mapper;
    private final CardLimitValidationSupport validation;

    public CardLimitActualServiceImpl(CardLimitActualRepository repository,
                                      CardLimitActualMapper mapper,
                                      CardLimitValidationSupport validation) {
        this.repository = repository;
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    @Transactional
    public CardLimitActualResponse create(CardLimitActualCreateRequest request) {
        Card card = validation.requireEligibleCard(request.getPan());
        String tranCode = validation.normalizeTranCode(request.getTranCode());
        String channelCode = request.getChannelCode().trim().toUpperCase();
        String pan = request.getPan().trim();

        if (repository.existsByPanAndChannelCodeAndTranCode(pan, channelCode, tranCode)) {
            throw new DuplicateResourceException("CardLimitActual", pan + "/" + channelCode + "/" + tranCode);
        }

        BigDecimal maxCeiling = validation.resolveMaxCeiling(card, pan, tranCode);
        validation.assertWithinCeiling(request.getAvailableLimit(), maxCeiling);

        CardLimitActual e = mapper.toEntity(request);
        e.setPan(pan);
        e.setChannelCode(channelCode);
        e.setTranCode(tranCode);
        LocalDate today = LocalDate.now();
        if (e.getCycleBeginDate() == null) e.setCycleBeginDate(today);
        String by = validation.auditUser(request.getCreatedBy());
        e.setCreatedOn(today);
        e.setUpdatedOn(today);
        e.setCreatedBy(by);
        e.setUpdatedBy(by);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    public List<CardLimitActualResponse> findByPan(String pan) {
        if (pan == null || pan.isBlank()) {
            throw new BusinessValidationException("pan query param is required");
        }
        return mapper.toResponseList(repository.findByPan(pan.trim()));
    }

    @Override
    public CardLimitActualResponse get(String pan, String channelCode, String tranCode) {
        if (pan == null || pan.isBlank() || channelCode == null || channelCode.isBlank() || tranCode == null || tranCode.isBlank()) {
            throw new BusinessValidationException("pan, channelCode and tranCode are required");
        }
        String tc = validation.normalizeTranCode(tranCode);
        String ch = channelCode.trim().toUpperCase();
        CardLimitActual e = repository.findByPanAndChannelCodeAndTranCode(pan.trim(), ch, tc)
            .orElseThrow(() -> new ResourceNotFoundException("CardLimitActual", pan + "/" + ch + "/" + tc));
        return mapper.toResponse(e);
    }

    @Override
    @Transactional
    public CardLimitActualResponse update(CardLimitActualUpdateRequest request) {
        Card card = validation.requireEligibleCard(request.getPan());
        String pan = request.getPan().trim();
        String channelCode = request.getChannelCode().trim().toUpperCase();
        String tranCode = validation.normalizeTranCode(request.getTranCode());

        CardLimitActual e = repository.findByPanAndChannelCodeAndTranCode(pan, channelCode, tranCode)
            .orElseThrow(() -> new ResourceNotFoundException("CardLimitActual", pan + "/" + channelCode + "/" + tranCode));

        BigDecimal maxCeiling = validation.resolveMaxCeiling(card, pan, tranCode);
        validation.assertWithinCeiling(request.getAvailableLimit(), maxCeiling);
        // Same row update — customer can only decrease available limit
        validation.assertDecreaseOnly(e.getAvailableLimit(), request.getAvailableLimit());

        e.setAvailableLimit(request.getAvailableLimit());
        if (request.getAvailableTranCount() != null) {
            if (request.getAvailableTranCount() < 0) {
                throw new BusinessValidationException("availableTranCount cannot be negative");
            }
            if (e.getAvailableTranCount() != null && request.getAvailableTranCount() > e.getAvailableTranCount()) {
                throw new BusinessValidationException(
                    "Customer can only decrease availableTranCount; current="
                        + e.getAvailableTranCount() + ", requested=" + request.getAvailableTranCount());
            }
            e.setAvailableTranCount(request.getAvailableTranCount());
        }
        if (request.getCycleBeginDate() != null) {
            e.setCycleBeginDate(request.getCycleBeginDate());
        }
        e.setUpdatedOn(LocalDate.now());
        e.setUpdatedBy(validation.auditUser(request.getUpdatedBy()));
        return mapper.toResponse(repository.save(e));
    }
}
