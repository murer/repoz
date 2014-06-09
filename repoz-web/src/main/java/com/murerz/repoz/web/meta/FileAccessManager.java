package com.murerz.repoz.web.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StaticRepozFile;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.Util;

public class FileAccessManager implements AccessManager {

	private User parseLine(String line) {
		String[] array = line.split("\\s+");
		String path = array[0];
		String user = array[1];
		String type = array[2];
		String pass = array[3];

		User ret = new User().setPath(path).setUser(user).setType(type).setPass(pass);
		return ret;
	}

	public int auth(String username, String password, String path, String accessType) {
		RepozAuth auth = load();
		if (username == null) {
			username = "anonymous";
			password = "x";
		}
		User user = auth.searchPath(path, username);
		if (user == null) {
			return 403;
		}
		if (!user.getType().equals(accessType) && !("write".equals(user.getType()) && "read".equals(accessType))) {
			return 403;
		}
		if (!checkPassword(user.getPass(), password)) {
			return 401;
		}
		return 200;
	}

	private boolean checkPassword(String expected, String password) {
		return password.equals(expected);
	}

	public void save(User user) {
		RepozAuth auth = load();
		auth.setUser(user);
		save(auth);
	}

	private void save(RepozAuth auth) {
		FileSystem fs = FileSystemFactory.create();

		StringBuilder sb = new StringBuilder();
		Collection<User> values = auth.getAuths().values();
		for (User user : values) {
			sb.append(user.getPath());
			sb.append(" ").append(user.getUser());
			sb.append(" ").append(user.getType());
			sb.append(" ").append(user.getPass());
			sb.append("\n");
		}

		Util.validateUsascii(sb);

		RepozFile file = new StaticRepozFile().setData(sb.toString().getBytes()).setCharset("UTF-8").setMediaType("text/plain").setPath(RepozUtil.ACCESS);
		fs.save(file);
	}

	private RepozAuth load() {
		FileSystem fs = FileSystemFactory.create();
		RepozFile file = fs.read(RepozUtil.ACCESS);
		RepozAuth ret = new RepozAuth();
		if(file == null) {
			return ret;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(file.getIn()));
		try {
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				Util.validateUsascii(line);
				if (line.length() < 0 || line.startsWith("#")) {
					continue;
				}
				User user = parseLine(line);
				ret.setUser(user);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
		}
	}

}
