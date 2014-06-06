package com.murerz.repoz.web.meta;

public interface AccessManager {

	void update(String path, String user, String pass, String type);

	int auth(String username, String password, String path, String accessType);

}
