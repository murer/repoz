package com.murerz.repoz.web.fs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.FlexJson;
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
			ret.setPath(path).setMediaType(mediaType);

			String m = Util.read(new File(file.getPath() + RepozUtil.REPOZMETA).toURI().toURL(), "UTF-8");
			FileMeta meta = FlexJson.instance().parse(m, FileMeta.class);
			ret.setCharset(meta.getCharset()).setMediaType(meta.getMediaType());

			ret.setIn(new BufferedInputStream(new FileInputStream(file)));
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(RepozFile file) {
		File f = new File(base(), file.getPath());
		File parent = f.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		File meta = new File(f.getPath() + RepozUtil.REPOZMETA);
		FileMeta m = new FileMeta(file.getMediaType(), file.getCharset());
		Util.write(meta, FlexJson.instance().format(m));
		copyBinary(file, f);
	}

	private void copyBinary(RepozFile file, File f) {
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
		File meta = new File(f + RepozUtil.REPOZMETA);
		if (f.exists()) {
			Util.deleteRecursively(f);
		}
		if (meta.exists()) {
			Util.deleteRecursively(meta);
		}
	}

	public void deleteAll() {
		File base = base();
		LOG.info("Deleting all: " + base);
		Util.deleteChildsRecursively(base);
	}

}
