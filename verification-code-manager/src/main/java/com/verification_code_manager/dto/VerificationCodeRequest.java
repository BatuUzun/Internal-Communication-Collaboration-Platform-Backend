package com.verification_code_manager.dto;

import com.verification_code_manager.entity.VerificationCode.RequestType;

import jakarta.validation.constraints.NotNull;

public class VerificationCodeRequest {
	
    @NotNull(message = "User ID cannot be null or blank.")
    private Long userId;
    
    @NotNull(message = "Request type cannot be null.")
    private RequestType requestType;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
