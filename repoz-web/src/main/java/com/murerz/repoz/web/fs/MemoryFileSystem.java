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

	public Set<String> list(String path) {
		if ("/".equals(path)) {
			return listRepositories();
		}
		Set<String> ret = new TreeSet<String>();
		Set<String> set = files.keySet();
		for (String p : set) {
			if (p.length() > path.length() && p.startsWith(path)) {
				String result = p.replaceAll("^(.{" + path.length() + "," + path.length() + "}/[^/]+).*$", "$1");
				ret.add(result);
			}
		}
		return ret;
	}

	public Set<String> listRecursively(String path) {
		Set<String> ret = new TreeSet<String>();
		Set<String> set = files.keySet();
		for (String p : set) {
			if (p.length() > path.length() && p.startsWith(path)) {
				Set<String> explode = explode(p);
				for (String exp : explode) {
					if (exp.length() > path.length()) {
						ret.add(exp);
					}
				}
			}
		}
		return ret;
	}

	private Set<String> explode(String p) {
		Set<String> ret = new TreeSet<String>();
		StringBuilder sb = new StringBuilder();
		String[] array = p.split("/");
		for (int i = 1; i < array.length; i++) {
			String path = array[i];
			sb.append("/").append(path);
			ret.add(sb.toString());
		}
		return ret;
	}

	public MetaFile head(String path) {
		return read(path);
	}

}
