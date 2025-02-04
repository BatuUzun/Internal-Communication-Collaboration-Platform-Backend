package com.verification_code_manager.dto;

import com.verification_code_manager.entity.VerificationCode;
import com.verification_code_manager.entity.VerificationCode.RequestType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VerificationCodeValidationRequest {
	
	@NotNull(message = "User ID cannot be null.")
    private Long userId;
	
    @NotNull(message = "Verification code cannot be null.")
    @Pattern(regexp = "\\d{6}", message = "Verification code must be exactly 6 numeric digits.")
    private String verificationCode;
	
	@NotNull(message = "Request type cannot be null.")
    private RequestType requestType;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public VerificationCode.RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(VerificationCode.RequestType requestType) {
        this.requestType = requestType;
    }
}