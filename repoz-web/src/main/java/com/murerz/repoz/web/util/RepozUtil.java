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
		
	}

	public static void validateUser(String user) {
		
	}

	public static void validatePass(String pass) {
		
	}

	public static void validateAccessType(String type) {
		
	}

}
