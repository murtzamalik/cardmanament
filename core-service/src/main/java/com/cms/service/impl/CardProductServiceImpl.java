package com.cms.service.impl;

import com.cms.dal.entity.CardProduct;
import com.cms.dal.repository.CardProductRepository;
import com.cms.dto.request.CardProductCreateRequest;
import com.cms.dto.request.CardProductUpdateRequest;
import com.cms.dto.response.CardProductResponse;
import com.cms.exception.DuplicateResourceException;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.CardProductMapper;
import com.cms.service.CardProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardProductServiceImpl implements CardProductService {

    private final CardProductRepository cardProductRepository;
    private final CardProductMapper cardProductMapper;

    public CardProductServiceImpl(CardProductRepository cardProductRepository, CardProductMapper cardProductMapper) {
        this.cardProductRepository = cardProductRepository;
        this.cardProductMapper = cardProductMapper;
    }

    @Override
    @Transactional
    public CardProductResponse create(CardProductCreateRequest request) {
        if (request.getProductCode() != null && cardProductRepository.findByProductCode(request.getProductCode()).isPresent())
            throw new DuplicateResourceException("CardProduct", request.getProductCode());
        CardProduct e = cardProductMapper.toEntity(request);
        e.setCreatedOn(LocalDateTime.now());
        e.setUpdatedOn(LocalDateTime.now());
        return cardProductMapper.toResponse(cardProductRepository.save(e));
    }

    @Override
    public List<CardProductResponse> findAll() {
        return cardProductMapper.toResponseList(cardProductRepository.findAllByOrderByProductCode());
    }

    @Override
    public CardProductResponse getById(Long id) {
        CardProduct e = cardProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardProduct", String.valueOf(id)));
        return cardProductMapper.toResponse(e);
    }

    @Override
    @Transactional
    public CardProductResponse update(Long id, CardProductUpdateRequest request) {
        CardProduct e = cardProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CardProduct", String.valueOf(id)));
        if (request.getProductName() != null) e.setProductName(request.getProductName());
        if (request.getIsActive() != null) e.setIsActive(Boolean.TRUE.equals(request.getIsActive()) ? 1 : 0);
        e.setUpdatedOn(LocalDateTime.now());
        return cardProductMapper.toResponse(cardProductRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cardProductRepository.existsById(id))
            throw new ResourceNotFoundException("CardProduct", String.valueOf(id));
        cardProductRepository.deleteById(id);
    }
}
