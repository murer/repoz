package com.murerz.repoz.web.fs;

import com.murerz.repoz.web.util.ReflectionUtil;

public class FileSystemFactory {

	public static FileSystem create() {
		String className = System.getProperty("repoz.accessmanager.impl", "com.murerz.repoz.web.fs.MemoryFileSystem");
		return (FileSystem) ReflectionUtil.newInstance(className);
	}

}
