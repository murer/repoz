package com.murerz.repoz.web.util;

public class SecurityHelper {

	private static final Object MUTEX = new Object();
	private static SecurityHelper me = null;

	public static SecurityHelper me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					me = new SecurityHelper();
				}
			}
		}
		return me;
	}

	public String sign(String token) {
		return token;
	}

	public String unsign(String token) {
		return token;
	}

}
