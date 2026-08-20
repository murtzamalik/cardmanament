package com.cms.dal.repository;

import com.cms.dal.entity.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardProductRepository extends JpaRepository<CardProduct, Long> {

    Optional<CardProduct> findByProductCode(String productCode);

    List<CardProduct> findByIsActive(Integer isActive);

    List<CardProduct> findAllByOrderByProductCode();
}
