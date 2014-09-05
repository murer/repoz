package com.murerz.repoz.web.fs;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MetaFile {

	private String path;

	private String mediaType;

	private String charset;

	private String length;

	private Map<String, String> params = new TreeMap<String, String>();

	public String getPath() {
		return path;
	}

	public MetaFile setPath(String path) {
		this.path = path;
		return this;
	}

	public String getMediaType() {
		return mediaType;
	}

	public MetaFile setMediaType(String contentType) {
		this.mediaType = contentType;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public MetaFile setCharset(String charset) {
		this.charset = charset;
		return this;
	}

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

	public MetaFile setParams(Map<String, String> params) {
		this.params.clear();
		if (params != null) {
			this.params.putAll(params);
		}
		return this;
	}

	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(params);
	}

	public MetaFile setParam(String n, String v) {
		params.put(n, v);
		return this;
	}

	public String getLength() {
		return length;
	}

	public MetaFile setLength(String length) {
		this.length = length;
		return this;
	}

}
