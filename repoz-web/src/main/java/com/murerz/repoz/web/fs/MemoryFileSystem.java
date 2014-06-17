package com.murerz.repoz.web.fs;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
		Set<String> set = new HashSet<String>(files.keySet());
		for (String p : set) {
			if (p.startsWith(path)) {
				files.remove(p);
			}
		}
	}

	public void deleteAll() {
		files.clear();
	}

	public Set<String> listRepositories() {
		Set<String> ret = new TreeSet<String>();
		Set<String> set = files.keySet();
		for (String path : set) {
			String[] name = path.split("/");
			ret.add("/" + name[1]);
		}
		return ret;
	}

}
