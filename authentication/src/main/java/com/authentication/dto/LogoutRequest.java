package com.authentication.dto;

public class LogoutRequest {
	private Long userId;

	public LogoutRequest(Long userId) {
		super();
		this.userId = userId;
	}

	public LogoutRequest() {
		super();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}
