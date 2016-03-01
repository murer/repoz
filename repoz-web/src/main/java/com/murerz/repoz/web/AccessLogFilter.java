package com.murerz.repoz.web;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.util.AccessLogger;
import com.murerz.repoz.web.util.CTX;
import com.murerz.repoz.web.util.LogResponse;
import com.murerz.repoz.web.util.Util;

public class AccessLogFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AccessLogFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
		List<Object> list = toLogString(req);
		LogResponse logResponse = new LogResponse(resp);
		Exception exp = null;
		try {
			chain.doFilter(req, logResponse);
		} catch (Exception e) {
			exp = e;
			throw new RuntimeException(e);
		} finally {
			try {
				list.addAll(toLogString(logResponse));
				AccessLogger me = AccessLogger.me();
				me.log(list, exp);
			} catch (Exception e) {
				LOG.error("error wring access log", e);
			}
		}
	}

	private Collection<? extends Object> toLogString(LogResponse resp) {
		List<Object> list = new ArrayList<Object>();
		list.add(Util.nowZ());
		list.add(resp.getCode());
		list.add(resp.getContentType());
		list.add(resp.getLen());
		return list;
	}

	private List<Object> toLogString(HttpServletRequest req) {
		List<Object> list = new ArrayList<Object>();
		list.add(Util.nowZ());
		list.add(CTX.getAsString("username"));
		list.add(req.getRemoteAddr());
		list.add(req.getHeader("X-Forwarded-For"));
		list.add(req.getHeader("X-Repoz-Schema"));
		list.add(req.getMethod());
		list.add(req.getRequestURI());
		list.add(req.getQueryString());
		list.add(req.getContentType());
		list.add(req.getContentLength());
		list.add(req.getHeader("User-Agent"));
		list.add(req.getHeader("Referer"));
		return list;
	}

	public void destroy() {

	}

	public static void main(String[] args) {
		System.out.println(MessageFormat.format("0: {0}, 1: {1}, 2: {2}, 3: {3}, 4: {4}, 5: {5}", "a", "b", null, 4));
	}

}
