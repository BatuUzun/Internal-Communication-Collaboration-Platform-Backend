package com.tokengenerator.dto;

public class TokenGenerateResponseDTO {

    private String token; // Represent the token as a Base64 string for readability

    public TokenGenerateResponseDTO(String token) {
        this.token = token;
    }

    // Getters and setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}