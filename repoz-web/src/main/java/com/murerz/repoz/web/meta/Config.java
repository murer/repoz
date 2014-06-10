package com.murerz.repoz.web.meta;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.AccessBasicFilter;
import com.murerz.repoz.web.util.Util;

public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(AccessBasicFilter.class);

	private static Object MUTEX = new Object();

	private static Config me = null;

	private Properties props;

	public static Config me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					Config config = new Config();
					config.prepare();
					me = config;
				}
			}
		}
		return me;
	}

	private void prepare() {
		props = Util.properties(getClass().getClassLoader().getResource("repoz.properties"));
		if (props == null) {
			props = new Properties();
		}
		String userpass = Util.str(System.getProperty("repoz.access.userpass"));
		if (userpass != null) {
			String[] array = userpass.split(":");
			String user = array[0];
			String pass = array[1];
			LOG.info("repoz.access.userpass: " + user);
			props.setProperty("repoz.access." + user, pass);
		}
	}

	public String password(String username) {
		return props.getProperty("repoz.access." + username);
	}

}
