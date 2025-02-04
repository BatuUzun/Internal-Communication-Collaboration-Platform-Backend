package com.verification_code_manager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.verification_code_manager.dto.VerificationCodeRequest;
import com.verification_code_manager.dto.VerificationCodeValidationRequest;
import com.verification_code_manager.entity.VerificationCode;
import com.verification_code_manager.service.UserService;
import com.verification_code_manager.service.VerificationCodeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/verification-code-manager")
public class VerificationCodeController {

	@Autowired
    private VerificationCodeService verificationCodeService;
	
	@Autowired
	private UserService userService;

    
    @PostMapping("/create-code")
    public ResponseEntity<?> createCode(@RequestBody @Valid VerificationCodeRequest request) {
        try {
            VerificationCode verificationCode = verificationCodeService.createVerificationCode(request.getUserId(), request.getRequestType());
            return ResponseEntity.ok(verificationCode);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred."
            ));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateCode(@RequestBody @Valid VerificationCodeValidationRequest request) {
        boolean isValid = verificationCodeService.isValidCode(
        		request.getUserId(), 
        		request.getVerificationCode(), 
        		request.getRequestType());
        
        if (isValid) {
            return ResponseEntity.ok().body("Verification code is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification code.");
        }
    }
    
    @GetMapping("/is-verified/{userId}")
    public ResponseEntity<?> checkUserVerificationStatus(@PathVariable Long userId) {
        try {
            boolean isVerified = userService.isUserVerified(userId);
            return ResponseEntity.ok(isVerified);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    
    
}