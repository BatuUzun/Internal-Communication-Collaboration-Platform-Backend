package com.verification_code_manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.verification_code_manager.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailById(@Param("userId") Long userId);
    
}