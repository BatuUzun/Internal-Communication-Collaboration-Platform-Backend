package com.verification_code_manager.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.verification_code_manager.entity.VerificationCode;

@Component
public class ExpirationTimeCalculator {

	@Autowired
    private VerificationExpirationConfig config;

    

    public LocalDateTime calculateExpirationTime(VerificationCode.RequestType requestType) {
        switch (requestType) {
            case VERIFY:
                return LocalDateTime.now().plusMinutes(config.getVerify());
            case CHANGE:
            	return LocalDateTime.now().plusMinutes(config.getChange());
            case RESET:
                return LocalDateTime.now().plusMinutes(config.getReset());
            default:
                throw new IllegalArgumentException("Unknown request type");
        }
    }
}
