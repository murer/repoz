package com.murerz.repoz.web.meta;

public class AccessManagerFactory {

	public static AccessManager create() {
		return new GrantAccessManager();
	}

}
