package com.cms.service.impl;

import com.cms.dal.entity.CardType;
import com.cms.dal.repository.CardProductRepository;
import com.cms.dal.repository.CardTypeRepository;
import com.cms.dal.repository.LimitProfileRepository;
import com.cms.dto.request.CardTypeCreateRequest;
import com.cms.dto.request.CardTypeUpdateRequest;
import com.cms.dto.response.CardTypeResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardTypeMapper;
import com.cms.service.CardTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import com.cms.dal.entity.CardType;
import com.cms.dal.entity.LimitProfile;
import com.cms.dal.repository.CardProductRepository;
import com.cms.dal.repository.CardTypeRepository;
import com.cms.dal.repository.LimitProfileRepository;
import com.cms.dto.request.CardTypeCreateRequest;
import com.cms.dto.request.CardTypeUpdateRequest;
import com.cms.dto.response.CardTypeResponse;
import com.cms.exception.BusinessValidationException;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardTypeMapper;
import com.cms.service.CardTypeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardTypeServiceImpl implements CardTypeService {

    private static final Logger log = LoggerFactory.getLogger(CardTypeServiceImpl.class);

    private final CardTypeRepository cardTypeRepository;
    private final CardProductRepository cardProductRepository;
    private final LimitProfileRepository limitProfileRepository;
    private final CardTypeMapper cardTypeMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public CardTypeServiceImpl(CardTypeRepository cardTypeRepository,
                               CardProductRepository cardProductRepository,
                               LimitProfileRepository limitProfileRepository,
                               CardTypeMapper cardTypeMapper) {
        this.cardTypeRepository = cardTypeRepository;
        this.cardProductRepository = cardProductRepository;
        this.limitProfileRepository = limitProfileRepository;
        this.cardTypeMapper = cardTypeMapper;
    }

    @Override
    @Transactional
    public CardTypeResponse create(CardTypeCreateRequest request) {
        if (request.getCardTypeCode() != null && cardTypeRepository.findByCardTypeCode(request.getCardTypeCode()).isPresent())
            throw new DuplicateResourceException("CardType", request.getCardTypeCode());
        if (request.getProductId() != null) {
            cardProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("CardProduct", String.valueOf(request.getProductId())));
        } else if (request.getProductCode() != null && !request.getProductCode().isBlank()) {
            if (cardProductRepository.findByProductCode(request.getProductCode()).isEmpty())
                throw new ResourceNotFoundException("CardProduct", request.getProductCode());
        } else {
            throw new BusinessValidationException("Either productId or productCode is required");
        }
        validateLimitProfileId(request.getDefaultLimitProfileId());
        CardType e = cardTypeMapper.toEntity(request);
        e.setCardTypeCode(mapCardTypeCode(request.getCardTypeCode()));
        if (request.getProductId() != null) {
            cardProductRepository.findById(request.getProductId())
                .ifPresent(p -> { e.setProductId(p.getId()); e.setProductCode(p.getProductCode()); });
        } else if (request.getProductCode() != null) {
            cardProductRepository.findByProductCode(request.getProductCode())
                .ifPresent(p -> { e.setProductId(p.getId()); e.setProductCode(p.getProductCode()); });
        }
        e.setDefaultLimitProfileId(request.getDefaultLimitProfileId());
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        CardType saved = cardTypeRepository.save(e);
        ensureCardLimitProfileChannels(saved.getDefaultLimitProfileId());
        return toEnrichedResponse(saved);
    }

    private String mapCardTypeCode(String code) {
        if (code == null) return null;
        return switch (code.toUpperCase().trim()) {
            case "DEBIT_STD", "DEBIT" -> "005";
            case "CREDIT_STD", "CREDIT" -> "006";
            case "TEST" -> "007";
            default -> code;
        };
    }

    private void validateLimitProfileId(Long limitProfileId) {
        if (limitProfileId == null) {
            return;
        }
        limitProfileRepository.findById(limitProfileId)
            .orElseThrow(() -> new ResourceNotFoundException("LimitProfile", String.valueOf(limitProfileId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardTypeResponse> findAll() {
        return cardTypeRepository.findAllByOrderByCardTypeCode().stream()
            .map(this::toEnrichedResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CardTypeResponse getById(Long id) {
        CardType e = cardTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(id)));
        return toEnrichedResponse(e);
    }

    private CardTypeResponse toEnrichedResponse(CardType e) {
        CardTypeResponse r = cardTypeMapper.toResponse(e);
        if (e.getDefaultLimitProfileId() != null) {
            limitProfileRepository.findById(e.getDefaultLimitProfileId()).ifPresent(lp -> {
                r.setDefaultLimitProfileCode(lp.getProfileCode());
                r.setDefaultLimitProfileName(lp.getProfileName());
            });
        }
        return r;
    }

    @Override
    @Transactional
    public CardTypeResponse update(Long id, CardTypeUpdateRequest request) {
        CardType e = cardTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardType", String.valueOf(id)));
        if (request.getProductId() != null) {
            cardProductRepository.findById(request.getProductId())
                .ifPresent(p -> { e.setProductCode(p.getProductCode()); e.setProductId(p.getId()); });
        } else if (request.getProductCode() != null) {
            cardProductRepository.findByProductCode(request.getProductCode())
                .ifPresent(p -> { e.setProductCode(p.getProductCode()); e.setProductId(p.getId()); });
        }
        if (request.getCardTypeName() != null) e.setCardTypeName(request.getCardTypeName());
        if (request.getIsActive() != null) e.setIsActive(Boolean.TRUE.equals(request.getIsActive()) ? 1 : 0);
        if (request.getSupplementaryAllowed() != null) e.setSupplementaryAllowed(request.getSupplementaryAllowed());
        if (request.getIsSuppType() != null) e.setIsSuppType(request.getIsSuppType());
        if (request.getSuppTypeCode() != null) e.setSuppTypeCode(request.getSuppTypeCode());
        if (request.getPanLength() != null) e.setPanLength(request.getPanLength());
        if (request.getBin() != null) e.setBin(request.getBin());
        if (request.getExpPeriod() != null) e.setExpPeriod(request.getExpPeriod());
        if (request.getPanSequenceName() != null) e.setPanSequenceName(request.getPanSequenceName());
        if (request.getPanSequenceLength() != null) e.setPanSequenceLength(request.getPanSequenceLength());
        // Always apply (including null = clear) — frontend sends the field on edit
        validateLimitProfileId(request.getDefaultLimitProfileId());
        e.setDefaultLimitProfileId(request.getDefaultLimitProfileId());
        e.setUpdatedOn(LocalDateTime.now());
        CardType saved = cardTypeRepository.save(e);
        ensureCardLimitProfileChannels(saved.getDefaultLimitProfileId());
        return toEnrichedResponse(saved);
    }

    /**
     * Ensure CARD_LIMIT_PROFILE has ATM/POS/Ecommerce rows for this LIMIT_PROFILE.id
     * so cms-app /limit/available can join by PROFILE_ID.
     */
    private void ensureCardLimitProfileChannels(Long limitProfileId) {
        if (limitProfileId == null) {
            return;
        }
        LimitProfile lp = limitProfileRepository.findById(limitProfileId).orElse(null);
        if (lp == null) {
            return;
        }
        String profileId = String.valueOf(lp.getId());
        upsertChannelRow(profileId, "1", "1", lp.getAtmDailyAmount());
        upsertChannelRow(profileId, "2", "2", lp.getPosDailyAmount());
        upsertChannelRow(profileId, "3", "3", lp.getEcommerceDailyAmount());
    }

    private void upsertChannelRow(String profileId, String channel, String tran, BigDecimal daily) {
        BigDecimal max = daily != null ? daily : BigDecimal.ZERO;
        try {
            Number count = (Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM CARD_LIMIT_PROFILE WHERE PROFILE_ID = :p AND CHANNEL_CODE = :c AND TRAN_CODE = :t")
                .setParameter("p", profileId)
                .setParameter("c", channel)
                .setParameter("t", tran)
                .getSingleResult();
            if (count != null && count.longValue() > 0) {
                entityManager.createNativeQuery(
                        "UPDATE CARD_LIMIT_PROFILE SET MAX_LIMIT = :m, SINGLE_TRAN_LIMIT = :m, IS_DEFAULT = 1 " +
                            "WHERE PROFILE_ID = :p AND CHANNEL_CODE = :c AND TRAN_CODE = :t")
                    .setParameter("m", max)
                    .setParameter("p", profileId)
                    .setParameter("c", channel)
                    .setParameter("t", tran)
                    .executeUpdate();
            } else {
                entityManager.createNativeQuery(
                        "INSERT INTO CARD_LIMIT_PROFILE (PROFILE_ID, CHANNEL_CODE, TRAN_CODE, MAX_LIMIT, SINGLE_TRAN_LIMIT, IS_DEFAULT) " +
                            "VALUES (:p, :c, :t, :m, :m, 1)")
                    .setParameter("p", profileId)
                    .setParameter("c", channel)
                    .setParameter("t", tran)
                    .setParameter("m", max)
                    .executeUpdate();
            }
        } catch (Exception ex) {
            log.warn("Could not sync CARD_LIMIT_PROFILE for profile {} channel {}: {}", profileId, channel, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cardTypeRepository.existsById(id))
            throw new ResourceNotFoundException("CardType", String.valueOf(id));
        cardTypeRepository.deleteById(id);
    }
}
