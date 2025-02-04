package com.apigateway.dto;

public class TokenRefreshRequestDTO {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

	public TokenRefreshRequestDTO(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public TokenRefreshRequestDTO() {
		super();
	}
    
    
}
