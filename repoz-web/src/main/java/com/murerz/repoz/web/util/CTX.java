package com.murerz.repoz.web.util;

import com.murerz.repoz.web.ThreadContextFilter;

public class CTX {

	public static void set(String name, String value) {
		ThreadContextFilter.ctx().put(name, value);
	}

	public static Object get(String name) {
		return ThreadContextFilter.ctx().get(name);
	}

	public static String getAsString(String name) {
		return (String) get(name);
	}

}
