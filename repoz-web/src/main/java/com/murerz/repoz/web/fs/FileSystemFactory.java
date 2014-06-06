package com.murerz.repoz.web.fs;

import com.murerz.repoz.web.util.ReflectionUtil;

public class FileSystemFactory {

	public static final String PROPERTY = "repoz.filesystem.impl";

	public static FileSystem create() {
		String className = System.getProperty(PROPERTY, MemoryFileSystem.class.getName());
		return (FileSystem) ReflectionUtil.newInstance(className);
	}

}
