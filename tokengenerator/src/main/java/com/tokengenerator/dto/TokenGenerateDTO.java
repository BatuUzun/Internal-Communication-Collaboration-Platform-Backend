package com.tokengenerator.dto;

import com.tokengenerator.exception.ValidTokenType;

import jakarta.validation.constraints.NotNull;

public class TokenGenerateDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @ValidTokenType
    private String tokenType;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}