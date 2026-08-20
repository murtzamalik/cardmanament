package com.cms.app.validation;

import com.cms.app.repository.CardProductRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductCodeValidator implements ConstraintValidator<ProductCode, String> {

    @Autowired
    private CardProductRepository cardProductRepository;

    @Override
    public boolean isValid(String productCode, ConstraintValidatorContext context) {
        if (productCode == null || productCode.isBlank()) {
            return false;
        }
        return cardProductRepository.findByProductCode(productCode).isPresent();
    }
}
