package com.murerz.repoz.web.meta;

public class GrantAccessManager implements AccessManager {

	public void update(String path, String user, String pass, String type) {

	}

	public int auth(String username, String password, String path, String accessType) {
		return 200;
	}

}
