package com.murerz.repoz.web.util;

public class SecurityHelper {

	private static final Object MUTEX = new Object();
	private static SecurityHelper me = null;
	private KPCrypt crypt;

	public static SecurityHelper me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					me = new SecurityHelper();
					me.prepare();
				}
			}
		}
		return me;
	}

	private void prepare() {
		crypt = KPCrypt.create();
	}

	public String sign(String token) {
		String sign = crypt.signB64UrlSafe(token);
		return "" + token + "." + sign;
	}

	public String unsign(String token) {
		String[] array = token.split("\\.");
		if (array.length != 2) {
			throw new RuntimeException("wrong: " + token);
		}
		String data = array[0];
		String sign = array[1];
		if (!crypt.verify(data, sign)) {
			return null;
		}
		return data;
	}

}
