package com.cms.app.repository;

import com.cms.app.entity.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardProductRepository extends JpaRepository<CardProduct, String> {
    Optional<CardProduct> findByProductCode(String productCode);

    List<CardProduct> findByIsActive(Integer isActive);
}
