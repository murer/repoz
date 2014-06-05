package com.murerz.repoz.web.fs;

import java.io.InputStream;

public class RepozFile {

	private String path;

	private String contentType;

	private String charset;

	private InputStream in;

	public String getPath() {
		return path;
	}

	public RepozFile setPath(String path) {
		this.path = path;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public RepozFile setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public RepozFile setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public InputStream getIn() {
		return in;
	}

	public RepozFile setIn(InputStream in) {
		this.in = in;
		return this;
	}

}
