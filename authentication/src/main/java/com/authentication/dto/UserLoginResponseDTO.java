package com.authentication.dto;

public class UserLoginResponseDTO {

    private Long id;
    private String email;
    private Boolean isVerified;
    private String token;

    public UserLoginResponseDTO(Long id, String email, Boolean isVerified) {
        this.id = id;
        this.email = email;
        this.isVerified = isVerified;
    }
    
    public UserLoginResponseDTO(Long id, String email, Boolean isVerified, String token) {
        this.id = id;
        this.email = email;
        this.isVerified = isVerified;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public Boolean isVerified() {
		return isVerified;
	}

	public void setVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}
    
    
}
