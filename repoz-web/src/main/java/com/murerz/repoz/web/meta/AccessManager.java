package com.murerz.repoz.web.meta;

public interface AccessManager {

	int auth(User user);

	void save(User user);

	void delete(String repo, String username);

}
