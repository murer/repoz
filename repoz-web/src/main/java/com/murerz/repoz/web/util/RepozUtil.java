package com.murerz.repoz.web.util;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

public class RepozUtil {

	public static final String ACCESS = "/.repozauth.txt";

	public static String path(HttpServletRequest req) {
		String uri = ServletUtil.getURIWithoutContextPath(req);
		return uri.replaceAll("^/[^/]", "");
	}

	public static Properties config(String fileName) {
		Properties ret = Util.properties(RepozUtil.class.getClassLoader().getResource(fileName));
		if (ret == null) {
			ret = Util.properties(RepozUtil.class.getResource(fileName));
		}
		if (ret == null) {
			throw new RuntimeException("configuration not found: " + fileName);
		}
		return ret;
	}

	public static String mediaType(String path) {
		Properties mediaTypes = RepozUtil.config("repoz.mediatypes.properties");
		String ext = Util.extention(path);
		String ret = (ext == null ? null : mediaTypes.getProperty(ext));
		if (ret == null) {
			FileNameMap map = URLConnection.getFileNameMap();
			ret = map.getContentTypeFor(path);
		}
		return ret;
	}

	public static void validatePath(String path) {
		checkPattern(path, "^[a-zA-Z0-9/_\\.\\-\\$\\@]+$");
		checkFalsePattern(path, ".*//.*");
	}

	public static void checkPattern(String path, String pattern) {
		if (!path.matches(pattern)) {
			throw new RuntimeException("invalid: " + path + " " + pattern);
		}
	}

	public static void checkFalsePattern(String path, String pattern) {
		if (path.matches(pattern)) {
			throw new RuntimeException("invalid: " + path + " " + pattern);
		}
	}

	public static void validateUser(String user) {
		checkPattern(user, "^[a-zA-Z]{1,1}[a-zA-Z0-9_\\.\\-\\$\\@]{2,99}$");
	}

	public static void validatePass(String pass) {
		checkPattern(pass, "^[a-zA-Z0-9/_\\.\\-\\$\\@\\!\\?\\(\\)\\*\\%\\#\\=\\+\\[\\]\\{\\}\\,\\\\]{6,99}$");
	}

	public static void validateAccessType(String type) {
		if (!"read".equals(type) && !"write".equals(type)) {
			throw new RuntimeException("should be read or write: " + type);
		}
	}

}
