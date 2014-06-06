package com.murerz.repoz.web.meta;

public class FileAccessManager implements AccessManager {

	public boolean authenticate(String username, String password) {
		return false;
	}

	public boolean authorize(String username, String path, String accessType) {
		return false;
	}

}
