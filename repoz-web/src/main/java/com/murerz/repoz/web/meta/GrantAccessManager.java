package com.murerz.repoz.web.meta;

public class GrantAccessManager implements AccessManager {

	public boolean authenticate(String username, String password) {
		return true;
	}

	public boolean authorize(String username, String path, String accessType) {
		return true;
	}

}
