package com.cms.app.validation;

import com.cms.app.repository.CardStatusRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CardStatusCodeValidator implements ConstraintValidator<CardStatusCode, String> {

    @Autowired
    private CardStatusRepository cardStatusRepository;

    @Override
    public boolean isValid(String statusCode, ConstraintValidatorContext context) {
        if (statusCode == null || statusCode.isBlank()) {
            return false;
        }
        return cardStatusRepository.findByCardStatusCode(statusCode).isPresent();
    }
}
