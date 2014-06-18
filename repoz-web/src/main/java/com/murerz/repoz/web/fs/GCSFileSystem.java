package com.murerz.repoz.web.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.murerz.repoz.web.util.Util;
import com.murerz.repoz.web.util.XMLQuery;

public class GCSFileSystem implements FileSystem {

	public RepozFile read(String path) {
		try {
			HttpRequestFactory factory = GCSHandler.me().getFactory();
			GenericUrl url = GCSHandler.me().createURL(path);
			HttpRequest req = factory.buildGetRequest(url);
			req.setThrowExceptionOnExecuteError(false);
			HttpResponse resp = executeCheck(req, 200, 404);
			if (resp.getStatusCode() == 404) {
				return null;
			}
			StreamRepozFile ret = new StreamRepozFile().setIn(resp.getContent());
			ret.setPath(path);
			ret.setMediaType("text/plain");
			ret.setCharset("UTF-8");
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(RepozFile file) {
		try {
			HttpRequestFactory factory = GCSHandler.me().getFactory();
			GenericUrl url = GCSHandler.me().createURL(file.getPath());
			InputStreamContent content = new InputStreamContent(file.getContentType(), file.getIn());
			HttpRequest req = factory.buildPutRequest(url, content);
			executeCheck(req, 200);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpResponse executeCheck(HttpRequest req, int... codes) {
		try {
			HttpResponse resp = req.execute();
			int code = resp.getStatusCode();
			if (!checkCode(code, codes)) {
				throw new RuntimeException("gcs request error, expected: " + Arrays.toString(codes) + ", but was: " + resp.getStatusCode() + " " + resp.getStatusMessage() + ". "
						+ resp.parseAsString());
			}
			return resp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean checkCode(int code, int... codes) {
		for (int i : codes) {
			if (i == code) {
				return true;
			}
		}
		return false;
	}

	public void delete(String path) {
		try {
			HttpRequestFactory factory = GCSHandler.me().getFactory();
			GenericUrl url = GCSHandler.me().createURL(path);
			HttpRequest req = factory.buildDeleteRequest(url);
			executeCheck(req, 204, 404);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteAll() {
		Set<String> list = listRecursively("/");
		for (String path : list) {
			delete(path);
		}
	}

	public Set<String> listRepositories() {
		return null;
	}

	public Set<String> list(String path) {
		String sb = convertToPrefix(path);
		try {
			HttpRequestFactory factory = GCSHandler.me().getFactory();
			GenericUrl url = GCSHandler.me().createURL("/?delimiter=/&prefix=" + sb);
			HttpRequest req = factory.buildGetRequest(url);
			HttpResponse resp = executeCheck(req, 200);
			return parseFileNames(sb, resp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<String> listRecursively(String path) {
		String sb = convertToPrefix(path);
		try {
			HttpRequestFactory factory = GCSHandler.me().getFactory();
			GenericUrl url = GCSHandler.me().createURL("/?prefix=" + sb);
			HttpRequest req = factory.buildGetRequest(url);
			HttpResponse resp = executeCheck(req, 200);
			return parseFileNames(sb, resp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<String> parseFileNames(String sb, HttpResponse resp) throws IOException {
		InputStream in = null;
		try {
			in = resp.getContent();
			XMLQuery query = XMLQuery.parse(in, false);
			XMLQuery list = query.find("//Contents/Key/text() | //CommonPrefixes/Prefix/text()");
			Set<String> ret = new TreeSet<String>();
			for (int i = 0; i < list.size(); i++) {
				String path = list.get(i).getContent();
				path = path.replaceAll("/+$", "");
				ret.add("/" + path);
			}
			return ret;
		} finally {
			Util.close(in);
		}
	}

	private String convertToPrefix(String path) {
		if (!path.startsWith("/")) {
			throw new RuntimeException("wrong: " + path);
		}
		StringBuilder sb = new StringBuilder().append(path);
		sb.deleteCharAt(0);
		sb.append("/");
		String ret = sb.toString();
		ret = ret.replaceAll("^/+", "");
		return ret;
	}

}
