package com.verification_code_manager.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.verification_code_manager.config.ExpirationTimeCalculator;
import com.verification_code_manager.entity.VerificationCode;
import com.verification_code_manager.entity.VerificationCode.RequestType;
import com.verification_code_manager.entity.VerificationCode.Status;
import com.verification_code_manager.proxy.EmailSenderProxy;
import com.verification_code_manager.repository.VerificationCodeRepository;
import com.verification_code_manager.utils.CodeGenerator;

@Service
public class VerificationCodeService {

	@Autowired
	private VerificationCodeRepository repository;
	
	@Autowired
	private EmailSenderProxy emailSenderProxy;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private ExpirationTimeCalculator expirationTimeCalculator;

	public VerificationCode createVerificationCode(Long userId, RequestType requestType) {
	    // Validate request type
	    if (requestType != RequestType.CHANGE && requestType != RequestType.RESET && requestType != RequestType.VERIFY) {
	        throw new IllegalArgumentException("Invalid request type. Must be 'CHANGE', 'VERIFY' or 'RESET'.");
	    }

	    // Check for an existing pending code
	    Optional<VerificationCode> existingCode = repository.findByUserIdAndRequestTypeAndStatus(userId, requestType, Status.PENDING);
	    existingCode.ifPresent(repository::delete);

	    // Generate a new verification code
	    String code = CodeGenerator.generateCode();

	    VerificationCode verificationCode = new VerificationCode();
	    verificationCode.setUserId(userId);
	    verificationCode.setVerificationCode(code);
	    verificationCode.setExpirationTime(
	            expirationTimeCalculator.calculateExpirationTime(requestType)
	    );
	    verificationCode.setRequestType(requestType);
	    verificationCode.setStatus(Status.PENDING);

	    // Fetch the user's email and send the verification code
	    String email = userService.getEmailByUserId(userId);
	    if (email == null || email.isEmpty()) {
	        throw new IllegalArgumentException("No email found for the given user ID.");
	    }

	    // Handle the email sending logic based on request type
	    if (requestType == RequestType.CHANGE || requestType == RequestType.RESET) {
	        emailSenderProxy.sendChangePasswordCode(email, code);
	    } else if (requestType == RequestType.VERIFY) {
	        // Check if the user is already verified
	        boolean isVerified = userService.isUserVerified(userId);
	        if (isVerified) {
	            throw new IllegalArgumentException("User is already verified. Verification code will not be sent.");
	        }

	        emailSenderProxy.sendVerificationCode(email, code);
	    }

	    // Save and return the verification code
	    return repository.save(verificationCode);
	}

    
    public boolean isValidCode(Long userId, String verificationCode, VerificationCode.RequestType requestType) {
    	Optional<VerificationCode> optionalCode = repository.findValidCode(
    	        userId,
    	        verificationCode,
    	        requestType,
    	        LocalDateTime.now()
    	    );

    	if (optionalCode.isPresent()) {
    	        // Mark the code as USED to prevent reuse
    	        VerificationCode code = optionalCode.get();
    	        code.setStatus(VerificationCode.Status.USED);
    	        repository.save(code); // Update the status in the database
    	        
    	        if (requestType == VerificationCode.RequestType.VERIFY) {
                    userService.updateUserVerificationStatus(userId);
                }
    	        
    	        return true;
    	    }

    	    return false;
    }
}