package com.murerz.repoz.web.util;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

public class RepozUtil {

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
		return null;
	}

	public static String mediaType(String path) {
		Properties mediaTypes = RepozUtil.config("repoz.mediatypes.properties");
		String ext = Util.extention(path);
		if (ext == null) {
			return null;
		}
		return mediaTypes.getProperty(ext);
	}

}
