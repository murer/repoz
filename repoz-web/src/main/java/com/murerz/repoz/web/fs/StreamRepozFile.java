package com.murerz.repoz.web.fs;

import java.io.InputStream;

public class StreamRepozFile extends RepozFile {

	private InputStream in;

	@Override
	public InputStream getIn() {
		return in;
	}

	public StreamRepozFile setIn(InputStream in) {
		this.in = in;
		return this;
	}

}
