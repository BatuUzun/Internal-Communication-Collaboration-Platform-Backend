package com.tokengenerator.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tokengenerator.service.TokenService;

@Component
public class TokenCleanupScheduler {

    @Autowired
    private TokenService tokenService;

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanupExpiredTokens() {
        tokenService.deleteExpiredTokens();
    }
}