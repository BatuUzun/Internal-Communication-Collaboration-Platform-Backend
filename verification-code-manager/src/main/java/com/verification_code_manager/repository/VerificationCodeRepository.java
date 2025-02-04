package com.verification_code_manager.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.verification_code_manager.entity.VerificationCode;
import com.verification_code_manager.entity.VerificationCode.RequestType;
import com.verification_code_manager.entity.VerificationCode.Status;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserIdAndRequestTypeAndStatus(Long userId, RequestType requestType, Status status);
    
    @Query("SELECT vc FROM VerificationCode vc " +
    	       "WHERE vc.userId = :userId " +
    	       "AND vc.verificationCode = :verificationCode " +
    	       "AND vc.requestType = :requestType " +
    	       "AND vc.expirationTime > :currentTime " +  // Pass UTC time explicitly
    	       "AND vc.status = 'PENDING'")
    	Optional<VerificationCode> findValidCode(
    	    @Param("userId") Long userId,
    	    @Param("verificationCode") String verificationCode,
    	    @Param("requestType") VerificationCode.RequestType requestType,
    	    @Param("currentTime") LocalDateTime currentTime
    	);


}