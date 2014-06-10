package com.murerz.repoz.web.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StaticRepozFile;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.Util;

public class FileAccessManager implements AccessManager {

	public int auth(User user) {
		String path = user.getPath();
		String username = user.getUser();
		String password = user.getPass();
		String type = user.getType();
		String repo = repository(path);

		if (username == null) {
			username = "anonymous";
			password = "anonymous";
		}

		RepozAuths auths = loadFile(repo);

		User saved = auths.get(username);

		if (saved == null || !password.equals(saved.getPass())) {
			return 401;
		}
		if ("write".equals(type) && "read".equals(saved.getType())) {
			return 403;
		}
		return 200;
	}

	private String repository(String path) {
		String[] array = path.split("/");
		return "/" + array[1];
	}

	private RepozAuths loadFile(String path) {
		RepozAuths ret = new RepozAuths(path);
		FileSystem fs = FileSystemFactory.create();
		RepozFile file = fs.read(path + RepozUtil.ACCESS);
		if (file == null) {
			return ret;
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(file.getIn(), "UTF-8"));
			String line = "";
			while (true) {
				line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.startsWith("#")) {
					continue;
				}
				User user = parseLine(line);
				ret.put(user);
			}
			return ret;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
		}
	}

	private User parseLine(String line) {
		String[] array = line.split("\\s+");
		String repo = array[0];
		String username = array[1];
		String type = array[2];
		String password = array[3];
		User ret = new User().setPath(repo).setUser(username).setPass(password).setType(type);
		ret.validate();
		return ret;
	}

	public void save(User user) {
		String path = user.getPath();
		String repo = repository(path);
		RepozAuths auths = loadFile(repo);
		auths.put(user);
		saveFile(auths);
	}

	private void saveFile(RepozAuths auths) {
		try {
			FileSystem fs = FileSystemFactory.create();
			StaticRepozFile file = new StaticRepozFile();
			file.setPath(auths.getPath() + RepozUtil.ACCESS).setCharset("UTF-8").setMediaType("plain/text");
			Collection<User> set = auths.getUsers().values();
			StringBuilder sb = new StringBuilder();
			for (User user : set) {
				sb.append(user.getPath());
				sb.append(" ").append(user.getUser());
				sb.append(" ").append(user.getType());
				sb.append(" ").append(user.getPass());
				sb.append("\n");
			}
			byte[] data = sb.toString().getBytes("UTF-8");
			file.setData(data);
			fs.save(file);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(String repo, String username) {
		RepozAuths auths = loadFile(repo);
		if (auths == null) {
			return;
		}
		auths.remove(username);
		saveFile(auths);
	}

}
