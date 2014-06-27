package com.murerz.repoz.web.meta;

import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.Util;

public class User {

	private String path;

	private String user;

	private String type;

	private String pass;

	public String getPath() {
		return path;
	}

	public User setPath(String path) {
		this.path = path;
		return this;
	}

	public String getUser() {
		return user;
	}

	public User setUser(String user) {
		this.user = user;
		return this;
	}

	public String getType() {
		return type;
	}

	public User setType(String type) {
		this.type = type;
		return this;
	}

	public String getPass() {
		return pass;
	}

	public User setPass(String pass) {
		this.pass = pass;
		return this;
	}

	public void validate() {
		RepozUtil.validatePath(path);
		RepozUtil.checkFalsePattern(path, ".+/$");
		RepozUtil.validateUser(user);
		RepozUtil.validateAccessType(type);
		RepozUtil.validatePass(pass);
	}

	@Override
	public String toString() {
		return "[User " + path + " " + user + " " + type + "]";
	}
	
	

}
