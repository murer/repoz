package com.murerz.repoz.web.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.taglibs.standard.lang.jstl.test.StaticFunctionTests;

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
			password = "123";
		}

		RepozAuths auths = loadFile(repo);

		User saved = auths.get(repo, username);

		if (saved == null || !password.equals(user.getPass())) {
			return 401;
		}
		if ("write".equals(type) && "read".equals(user.getType())) {
			return 403;
		}
		return 200;
	}

	private String repository(String path) {
		String[] array = path.split("/");
		return "/" + array[1];
	}

	private RepozAuths loadFile(String path) {
		RepozAuths ret = new RepozAuths();
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
		String password = array[2];
		String type = array[3];
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
		FileSystem fs = FileSystemFactory.create();
		StaticRepozFile file = new StaticRepozFile();
		fs.save(file);
	}

}

