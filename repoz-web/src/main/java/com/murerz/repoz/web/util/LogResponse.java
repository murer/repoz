package com.murerz.repoz.web.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class LogResponse extends HttpServletResponseWrapper {

	private static final class LogOutpuStream extends ServletOutputStream {

		private final ServletOutputStream out;
		private int len;

		public LogOutpuStream(ServletOutputStream out) {
			this.out = out;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			this.len++;
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
			this.len += b.length;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.len += len;
		}

	}

	private Integer code = null;
	private LogOutpuStream out;

	public LogResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		super.sendError(sc, msg);
		code = sc;
	}

	@Override
	public void sendError(int sc) throws IOException {
		super.sendError(sc);
		code = sc;
	}

	@Override
	public void setStatus(int sc) {
		super.setStatus(sc);
		code = sc;
	}

	@Override
	public void setStatus(int sc, String sm) {
		super.setStatus(sc, sm);
		code = sc;
	}

	public Integer getCode() {
		return code;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new LogOutpuStream(super.getOutputStream());
		}
		return out;
	}

	public Integer getLen() {
		if (out == null) {
			return null;
		}
		return out.len;
	}

}
