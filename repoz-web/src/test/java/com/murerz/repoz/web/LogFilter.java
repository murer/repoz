package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

	private static class MyResponse extends HttpServletResponseWrapper {

		private Integer status;
		private String statusMessage;

		public MyResponse(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setStatus(int sc) {
			this.status = sc;
			super.setStatus(sc);
		}

		@Override
		public void setStatus(int sc, String sm) {
			this.status = sc;
			this.statusMessage = sm;
			super.setStatus(sc, sm);
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			ret.append("[").append(status);
			append(ret, statusMessage);
			append(ret, getContentType());
			ret.append("]");
			return ret.toString();
		}

		private void append(StringBuilder ret, String str) {
			if (str != null && str.length() > 0) {
				ret.append(' ').append(str);
			}
		}

	}

	public void init(FilterConfig cfg) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String reqStr = reqToString(request);
		MyResponse resp = new MyResponse(response);
		try {
			chain.doFilter(request, resp);
		} finally {
			LOG.info("Request: " + reqStr + ": " + resp);
		}
	}

	private String reqToString(HttpServletRequest req) {
		StringBuilder ret = new StringBuilder();
		ret.append(req.getMethod()).append(" ").append(req.getRequestURI());
		if (req.getQueryString() != null && req.getQueryString().length() > 0) {
			ret.append("?").append(cut(req.getQueryString(), 30));
		}
		if (req.getContentType() != null && req.getContentType().length() > 0) {
			ret.append(" [").append(req.getContentType()).append(" ").append(" ").append(req.getContentLength()).append("]");
		}
		return ret.toString();

	}

	private String cut(String str, int max) {
		return str == null ? "null" : str.length() > max ? str.substring(0, max) : str;
	}

	public void destroy() {

	}

}
