package com.murerz.repoz.web.meta;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.util.Util;

public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

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
			LOG.info("repoz.properties not found");
			props = new Properties();
		}
		props.putAll(System.getProperties());
	}

	public String password(String username) {
		return getProperty("repoz.access." + username);
	}

	public String getProperty(String key) {
		return Util.str(props.getProperty(key));
	}

	public String getProperty(String key, String def) {
		return Util.str(props.getProperty(key, def));
	}

	public String retProperty(String key) {
		String ret = getProperty(key);
		if (ret == null) {
			throw new RuntimeException("configuration missing: " + key);
		}
		return ret;
	}

	public boolean getBoolean(String key) {
		return "true".equals(getProperty(key));
	}

	public String getBaseURL() {
		return retProperty("repoz.base.url");
	}

	public String getGoogleClientId() {
		return retProperty("repoz.google.clientid");
	}

	public String getGoogleSecret() {
		return retProperty("repoz.google.secret");
	}

}
