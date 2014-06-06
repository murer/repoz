package com.murerz.repoz.web.util;

public class UserPass {

	private String username;

	private String password;

	public String getUsername() {
		return username;
	}

	public UserPass setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserPass setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public String toString() {
		return username == null ? "null" : username;
	}
}
