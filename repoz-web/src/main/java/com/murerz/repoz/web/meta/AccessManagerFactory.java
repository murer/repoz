package com.murerz.repoz.web.meta;

import com.murerz.repoz.web.util.ReflectionUtil;

public class AccessManagerFactory {

	public static AccessManager create() {
		String className = System.getProperty("repoz.accessmanager.impl", "com.murerz.repoz.web.meta.GrantAccessManager");
		return (AccessManager) ReflectionUtil.newInstance(className);
	}

}
