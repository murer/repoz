package com.murerz.repoz.web.fs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StaticRepozFile extends RepozFile {

	private byte[] data;

	@Override
	public InputStream getIn() {
		return new ByteArrayInputStream(data);
	}

	public byte[] getData() {
		return data;
	}

	public StaticRepozFile setData(byte[] data) {
		if (data != null) {
			setLength(Integer.toString(data.length));
		}
		this.data = data;
		return this;
	}

}
