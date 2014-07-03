package com.murerz.repoz.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ThreadContextFilter implements Filter {

	private static final ThreadLocal<Map<String, Object>> ctxs = new ThreadLocal<Map<String, Object>>();

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ctxs.set(new HashMap<String, Object>());
		try {
			chain.doFilter(request, response);
		} finally {
			ctxs.set(null);
		}
	}

	public void destroy() {

	}

	public static Map<String, Object> ctx() {
		return ctxs.get();
	}

}
