package com.murerz.repoz.web.meta;

import java.util.Map;
import java.util.TreeMap;

import com.murerz.repoz.web.util.RepozUtil;

public class RepozAuths {

	private final Map<RepozAuthEntry, User> users = new TreeMap<RepozAuthEntry, User>();

	public User get(String repo, String user) {
		return users.get(new RepozAuthEntry(repo, user));
	}

	public void put(User user) {
		user.validate();
		RepozUtil.checkPattern(user.getPath(), "^/[^/]+$");
		RepozAuthEntry entry = new RepozAuthEntry(user.getPath(), user.getUser());
		users.put(entry, user);
	}

}
