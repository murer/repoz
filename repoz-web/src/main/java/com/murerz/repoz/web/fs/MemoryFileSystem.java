package com.murerz.repoz.web.fs;

import java.util.Hashtable;
import java.util.Map;

import com.murerz.repoz.web.util.Util;

public class MemoryFileSystem implements FileSystem {

	private static Map<String, RepozFile> files = new Hashtable<String, RepozFile>();

	public RepozFile read(String path) {
		return files.get(path);
	}

	public void save(RepozFile file) {
		file = createStaticFile(file);
		files.put(file.getPath(), file);
	}

	private RepozFile createStaticFile(RepozFile file) {
		byte[] data = Util.readAll(file.getIn());
		return new StaticRepozFile().setData(data).setPath(file.getPath()).setCharset(file.getCharset()).setContentType(file.getContentType());
	}

	public void delete(String path) {
		files.remove(path);
	}

}
