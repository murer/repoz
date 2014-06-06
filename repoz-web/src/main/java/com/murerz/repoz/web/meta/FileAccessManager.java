package com.murerz.repoz.web.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StaticRepozFile;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.Util;

public class FileAccessManager implements AccessManager {

	public void update(String path, String user, String pass, String type) {
		String p = path;
		if (path.endsWith("/")) {
			p += RepozUtil.ACCESS;
		} else {
			p += "/" + RepozUtil.ACCESS;
		}
		Map<String, UserAccess> map = readFile(p, new HashMap<String, UserAccess>());
		map.put(user, new UserAccess().setUsername(user).setPassword(pass).setType(type));
		saveFile(p, map);
	}

	private void saveFile(String path, Map<String, UserAccess> map) {
		try {
			StaticRepozFile file = new StaticRepozFile();
			file.setPath(path).setCharset("UTF-8").setMediaType("application/json");
			StringBuilder sb = new StringBuilder();
			for (UserAccess userAccess : map.values()) {
				sb.append(userAccess.getUsername()).append(':').append(userAccess.getPassword());
				sb.append(' ').append(userAccess.getType()).append('\n');
			}
			file.setData(sb.toString().getBytes("UTF-8"));
			FileSystem fs = FileSystemFactory.create();
			fs.save(file);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, UserAccess> readAllFile(String path) {
		Map<String, UserAccess> ret = new TreeMap<String, UserAccess>();
		String[] array = path.split("/");
		StringBuilder tmp = new StringBuilder();
		for (int i = 0; i < array.length - 1; i++) {
			String str = array[i];
			if (tmp.length() == 0 || tmp.charAt(tmp.length() - 1) != '/') {
				tmp.append('/');
			}
			tmp.append(str);
			if (tmp.charAt(tmp.length() - 1) != '/') {
				tmp.append('/');
			}
			String fileName = tmp.toString() + RepozUtil.ACCESS;
			readFile(fileName, ret);
		}
		return ret;
	}

	private Map<String, UserAccess> readFile(String path, Map<String, UserAccess> ret) {
		FileSystem fs = FileSystemFactory.create();
		RepozFile f = fs.read(path);
		if (f == null) {
			return ret;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(f.getIn()));
		try {
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.length() < 0 || line.startsWith("#")) {
					continue;
				}
				UserAccess userAccess = parseLine(line);
				ret.put(userAccess.getUsername(), userAccess);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
		}
	}

	private UserAccess parseLine(String line) {
		String[] array = line.split("\\s+");
		String basic = array[0];
		String type = array[1];
		array = basic.split(":");
		String username = array[0];
		String password = array[1];
		UserAccess ret = new UserAccess().setUsername(username).setPassword(password).setType(type);
		return ret;
	}

	public int auth(String username, String password, String path, String accessType) {
		Map<String, UserAccess> map = readAllFile(path);
		if (username == null) {
			username = "anonymous";
			password = "x";
		}
		UserAccess user = map.get(username);
		if (user == null) {
			return 403;
		}
		if (!user.getType().equals(accessType) && !("write".equals(user.getType()) && "read".equals(accessType))) {
			return 403;
		}
		if (!checkPassword(user.getPassword(), password)) {
			return 401;
		}
		return 200;
	}

	private boolean checkPassword(String expected, String password) {
		return password.equals(expected);
	}

}
