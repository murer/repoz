package com.murerz.repoz.web.fs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.Util;

public class RawFileSystem implements FileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(RawFileSystem.class);
	
	private File base() {
		String ret = Config.me().retProperty("repoz.rawfilesytem.base");
		File file = new File(ret);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (!file.exists()) {
			throw new RuntimeException("not found: " + file);
		}
		if (!file.isDirectory()) {
			throw new RuntimeException("it is not a directory: " + file);
		}
		return file;

	}

	public RepozFile read(String path) {
		try {
			File file = new File(base(), path);
			if (!file.exists() || file.isDirectory()) {
				return null;
			}
			String mediaType = RepozUtil.mediaType(file.getPath());
			StreamRepozFile ret = new StreamRepozFile();
			ret.setCharset("UTF-8").setPath(path).setMediaType(mediaType);
			ret.setIn(new BufferedInputStream(new FileInputStream(file)));
			return ret;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(RepozFile file) {
		File f = new File(base(), file.getPath());
		File parent = f.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(f));
			Util.copyAll(file.getIn(), out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(out);
		}
	}

	public void delete(String path) {
		File f = new File(base(), path);
		Util.deleteRecursively(f);
	}

	public void deleteAll() {
		File base = base();
		LOG.info("Deleting all: " + base);
		Util.deleteChildsRecursively(base);
	}

}
