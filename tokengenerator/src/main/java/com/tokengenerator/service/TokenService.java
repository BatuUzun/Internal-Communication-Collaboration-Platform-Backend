package com.tokengenerator.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tokengenerator.entity.Token;
import com.tokengenerator.repository.TokenRepository;
import com.tokengenerator.type.TokenType;
import com.tokengenerator.utils.UUIDUtils;

import jakarta.transaction.Transactional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    
    public Token createToken(Long userId, String tokenType) {
        Token token = new Token();
        
        // Generate UUID and convert to binary
        UUID uuid = UUID.randomUUID();
        byte[] binaryToken = UUIDUtils.toBinary(uuid);
        
        token.setToken(binaryToken);
        token.setUserId(userId);
        token.setExpiryDate(LocalDateTime.now().plus(TokenType.getExpiry(tokenType)));

        return tokenRepository.save(token);
    }
 
    @Transactional
    public void deleteTokenByValue(byte[] token) {
        tokenRepository.deleteByToken(token);
    }
   
    
    public boolean isValidRefreshToken(byte[] refreshToken) {
        
        Optional<Token> token = tokenRepository.findByToken(refreshToken);
        if (token.isEmpty()) {
            return false;
        }

        if (token.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }



    public Long extractUserIdFromToken(byte[] refreshToken) {
        Optional<Token> token = tokenRepository.findByToken(refreshToken);
        return token.map(Token::getUserId).orElse(null);
    }


    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.findAll().stream()
            .filter(token -> token.getExpiryDate().isBefore(LocalDateTime.now()))
            .forEach(token -> tokenRepository.deleteByToken(token.getToken()));
    }
}