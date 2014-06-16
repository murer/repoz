package com.murerz.repoz.web.meta;

import flexjson.JSON;

public class GoogleOAuthToken {

	private String accessToken;
	private Long expiresIn;
	private String tokenType;

	@JSON(name = "access_token")
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@JSON(name = "expires_in")
	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	@JSON(name = "token_type")
	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@Override
	public String toString() {
		return "[GoogleOAuthToken " + accessToken + " " + expiresIn + " " + tokenType + "]";
	}

}
