package com.murerz.repoz.web.fs;

import java.io.InputStream;

public abstract class RepozFile {

	private String path;

	private String mediaType;

	private String charset;

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

}
