package com.murerz.repoz.web.meta;

import com.murerz.repoz.web.util.ReflectionUtil;

public class AccessManagerFactory {

	public static final String PROPERTY = "repoz.accessmanager.impl";

	public static AccessManager create() {
		String className = System.getProperty(PROPERTY, FileAccessManager.class.getName());
		return (AccessManager) ReflectionUtil.newInstance(className);
	}

}
