package com.murerz.repoz.web.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.ReflectionUtil;

public class FileSystemFactory {

	private static final Logger LOG = LoggerFactory.getLogger(FileSystemFactory.class);

	public static final String PROPERTY = "repoz.filesystem.impl";

	private static final Object MUTEX = new Object();
	private static FileSystem fs;

	public static FileSystem create() {
		if (fs == null) {
			synchronized (MUTEX) {
				if (fs == null) {
					String className = Config.me().getProperty(PROPERTY, MemoryFileSystem.class.getName());
					LOG.info("Using filesystem: " + className);
					fs = (FileSystem) ReflectionUtil.newInstance(className);
				}
			}
		}
		return fs;
	}

	public static void reset() {
		if (fs != null) {
			synchronized (MUTEX) {
				if (fs != null) {
					fs = null;
				}
			}
		}
	}

}
