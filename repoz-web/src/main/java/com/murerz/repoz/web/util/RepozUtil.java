package com.murerz.repoz.web.util;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpResponse;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StaticRepozFile;
import com.murerz.repoz.web.meta.Config;

public class RepozUtil {

	private static final Logger LOG = LoggerFactory.getLogger(RepozUtil.class);

	public static final String REPOZMETA = ".repozmeta";
	public static final String ACCESS = ".repozauth.txt";

	public static String getBaseURL(HttpServletRequest req) {
		String baseURL = Config.me().getBaseURL();
		baseURL = baseURL.replaceAll("/+$", "");
		baseURL += req.getContextPath();
		baseURL = baseURL.replaceAll("/+$", "");
		return baseURL;
	}

	public static String getUrl(HttpServletRequest req, String path) {
		String base = getBaseURL(req);
		return base + path;
	}

	public static String path(HttpServletRequest req) {
		String uri = ServletUtil.getURIWithoutContextPath(req);
		String ret = uri.replaceAll("^/[^/]", "");
		validatePath(ret);
		return ret;
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

	public static RepozFile createStaticFile(RepozFile file) {
		byte[] data = Util.readAll(file.getIn());
		return (RepozFile) new StaticRepozFile().setData(data).setPath(file.getPath()).setCharset(file.getCharset()).setMediaType(file.getMediaType()).setParams(file.getParams());
	}

	public static void sendHtmlError(HttpServletResponse resp, String error) {
		StringBuilder str = new StringBuilder();
		str.append("<html><head><title>Repoz</title></head><body>");
		str.append("<h1>Error</h1>");
		if (error != null) {
			str.append("<p>").append(error).append("</p>");
		}
		str.append("<a href=\"index.html\">Voltar</a>");
		str.append("</body></html>");
		ServletUtil.writeHtml(resp, str);
	}

	public static String getOauthCallback(HttpServletRequest req) {
		return getUrl(req, "/oauth2google");
	}

	public static void close(HttpResponse resp) {
		if (resp != null) {
			try {
				resp.disconnect();
			} catch (IOException e) {
				LOG.error("error closing", e);
			}
		}
	}
}
