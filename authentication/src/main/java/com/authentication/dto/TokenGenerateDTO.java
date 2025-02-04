package com.authentication.dto;


public class TokenGenerateDTO {

    private Long userId;

    private String tokenType;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

	public TokenGenerateDTO(Long userId, String tokenType) {
		super();
		this.userId = userId;
		this.tokenType = tokenType;
	}
    
    
}