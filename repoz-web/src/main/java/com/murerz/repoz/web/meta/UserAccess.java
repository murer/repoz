package com.murerz.repoz.web.meta;

public class UserAccess {

	private String username;

	private String password;

	private String type;

	public String getUsername() {
		return username;
	}

	public UserAccess setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserAccess setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getType() {
		return type;
	}

	public UserAccess setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public String toString() {
		return "[" + username + " " + type + "]";
	}

}
