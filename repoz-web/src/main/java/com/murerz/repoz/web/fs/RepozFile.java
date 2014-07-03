package com.murerz.repoz.web.fs;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public abstract class RepozFile {

	private String path;

	private String mediaType;

	private String charset;

	private Map<String, String> params = new TreeMap<String, String>();

	public String getPath() {
		return path;
	}

	public RepozFile setPath(String path) {
		this.path = path;
		return this;
	}

	public String getMediaType() {
		return mediaType;
	}

	public RepozFile setMediaType(String contentType) {
		this.mediaType = contentType;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public RepozFile setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public abstract InputStream getIn();

	@Override
	public String toString() {
		return "[" + path + " " + mediaType + " " + charset + "]";
	}

	public String getContentType() {
		if (charset == null) {
			return mediaType;
		}
		return "" + mediaType + "; charset=" + charset;
	}

	public RepozFile setParams(Map<String, String> params) {
		this.params.clear();
		if (params != null) {
			this.params.putAll(params);
		}
		return this;
	}

	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(params);
	}

	public RepozFile setParam(String n, String v) {
		params.put(n, v);
		return this;
	}

}
