package com.murerz.repoz.web.meta;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class RepozAuth {

	private final Map<String, User> auths = new TreeMap<String, User>();

	public Map<String, User> getAuths() {
		return Collections.unmodifiableMap(auths);
	}

	public void setAuths(Map<String, User> auths) {
		this.auths.clear();
		this.auths.putAll(auths);
	}

	public User getUser(String path, String user) {
		String key = generateKey(path, user);
		return auths.get(key);
	}

	private String generateKey(String path, String user) {
		return "" + path + " " + user;
	}

	public void setUser(User user) {
		String key = generateKey(user);
		auths.put(key, user);
	}

	private String generateKey(User user) {
		String path = user.getPath();
		String username = user.getUser();
		return generateKey(path, username);
	}

	@Override
	public String toString() {
		return "[RepozAuth " + auths + "]";
	}

	public User searchPath(String path, String username) {
		StringBuilder sb = new StringBuilder(path);
		while (sb.length() > 0) {
			User user = getUser(sb.toString(), username);
			if (user != null) {
				return user;
			}
			int idx = sb.lastIndexOf("/");
			sb.delete(idx, sb.length());
		}
		return getUser("/", username);
	}

}
