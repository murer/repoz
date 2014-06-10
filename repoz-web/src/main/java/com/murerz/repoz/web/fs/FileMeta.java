package com.murerz.repoz.web.fs;

public class FileMeta {

	private String mediaType;
	private String charset;

	public FileMeta() {

	}

	public FileMeta(String mediaType, String charset) {
		this.mediaType = mediaType;
		this.charset = charset;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
