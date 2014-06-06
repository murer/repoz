package com.murerz.repoz.web.meta;

public interface AccessManager {

	boolean authenticate(String username, String password);

	boolean authorize(String username, String path, String accessType);

}
