package com.tokengenerator.type;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TokenType {
    private static final Map<String, Duration> tokenExpiryMap = new HashMap<>();

    static {
        tokenExpiryMap.put("ACCESS_TOKEN", Duration.ofDays(1));
        tokenExpiryMap.put("REFRESH_TOKEN", Duration.ofDays(7));
        tokenExpiryMap.put("RESET_PASSWORD", Duration.ofHours(2));
    }

    public static Duration getExpiry(String tokenType) {
        return tokenExpiryMap.getOrDefault(tokenType, Duration.ofHours(1)); // Default to 1 hour
    }
    
    public static boolean isValidTokenType(String tokenType) {
        return tokenExpiryMap.containsKey(tokenType);
    }
}