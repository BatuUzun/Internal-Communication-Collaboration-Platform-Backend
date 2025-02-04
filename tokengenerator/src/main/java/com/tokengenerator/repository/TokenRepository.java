package com.tokengenerator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tokengenerator.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByToken(byte[] token);
    
	void deleteByToken(byte[] token);
    
	void deleteByUserId(Long userId);
	
}