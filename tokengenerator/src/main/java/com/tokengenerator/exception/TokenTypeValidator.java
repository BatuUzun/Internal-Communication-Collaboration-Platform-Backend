package com.tokengenerator.exception;

import com.tokengenerator.type.TokenType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TokenTypeValidator implements ConstraintValidator<ValidTokenType, String> {

    @Override
    public boolean isValid(String tokenType, ConstraintValidatorContext context) {
        return TokenType.isValidTokenType(tokenType);
    }
}