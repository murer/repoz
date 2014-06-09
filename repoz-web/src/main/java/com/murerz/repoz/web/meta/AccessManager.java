package com.murerz.repoz.web.meta;

public interface AccessManager {

	int auth(String username, String password, String path, String accessType);

	void save(User user);

}
