package com.murerz.repoz.web.fs;

import java.util.Hashtable;
import java.util.Map;

import com.murerz.repoz.web.util.RepozUtil;

public class MemoryFileSystem implements FileSystem {

	private static Map<String, RepozFile> files = new Hashtable<String, RepozFile>();

	public RepozFile read(String path) {
		return files.get(path);
	}

	public void save(RepozFile file) {
		file = RepozUtil.createStaticFile(file);
		files.put(file.getPath(), file);
	}

	public void delete(String path) {
		files.remove(path);
	}

	public void deleteAll() {
		files.clear();
	}

}
