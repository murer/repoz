package com.murerz.repoz.web.meta;

import java.util.Properties;

import com.murerz.repoz.web.util.Util;

public class Config {

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
			throw new RuntimeException("properties not found");
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

	public String reqProperty(String key) {
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
		return reqProperty("repoz.base.url");
	}

	public String getGoogleClientId() {
		return reqProperty("repoz.google.clientid");
	}

	public String getGoogleSecret() {
		return reqProperty("repoz.google.secret");
	}

	public static void reset() {
		if (me != null) {
			synchronized (MUTEX) {
				if (me != null) {
					me = null;
				}
			}
		}
	}

	public String getGoogleCloudStorageBase() {
		String ret = getProperty("repoz.gcs.base");
		if (ret == null) {
			return "";
		}
		return ret;
	}

	public int reqPropertyInt(String key) {
		return Integer.parseInt(reqProperty(key));
	}

}
