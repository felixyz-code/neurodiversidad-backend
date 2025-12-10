package com.neurodiversidad.neurodiversidad_backend.auth;

import com.neurodiversidad.neurodiversidad_backend.user.UserDTO;

public class LoginResponse {

	private String accessToken;
	private String tokenType = "Bearer";
	private long expiresIn;
	private UserDTO user;

	public LoginResponse(String accessToken, long expiresIn, UserDTO user) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.user = user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public UserDTO getUser() {
		return user;
	}
}
