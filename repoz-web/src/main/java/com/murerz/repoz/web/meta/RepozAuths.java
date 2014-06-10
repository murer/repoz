package com.murerz.repoz.web.meta;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class RepozAuths {

	private final Map<String, User> users = new TreeMap<String, User>();

	private final String path;

	public RepozAuths(String path) {
		this.path = path;
	}

	public User get(String user) {
		return users.get(user);
	}

	public void put(User user) {
		if (!path.equals(user.getPath())) {
			throw new RuntimeException("wrong: " + path + " " + user.getPath());
		}
		user.validate();
		users.put(user.getUser(), user);
	}

	public String getPath() {
		return path;
	}

	public Map<String, User> getUsers() {
		return Collections.unmodifiableMap(users);
	}

	@Override
	public String toString() {
		return "[RepozAuths " + path + "]";
	}

	public void remove(String username) {
		users.remove(username);
	}

}
