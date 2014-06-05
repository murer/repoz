package com.murerz.repoz.web.util;

import javax.servlet.http.HttpServletRequest;

public class RepozUtil {

	public static String path(HttpServletRequest req) {
		String uri = ServletUtil.getURIWithoutContextPath(req);
		return uri.replaceAll("^/[^/]", "");
	}

}
