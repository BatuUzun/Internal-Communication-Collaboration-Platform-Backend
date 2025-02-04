package com.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserLoginDTO {
	@NotBlank(message = "Email is required.")
    @Email(message = "Invalid email address.")
    private String email;
	
	@NotBlank(message = "Password is required.")
	@Pattern(
	        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,30}$",
	        message = "Password must be 8-30 characters long, contain at least one uppercase letter, and one special character."
	    )
    private String password;
	
	private boolean rememberMe;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
