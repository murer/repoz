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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.murerz.repoz.web.meta.Config;

public class AccessGoogleFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AccessGoogleFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
		boolean disabled = Config.me().getBoolean("repoz.google.auth.disabled");
		if (disabled) {
			chain.doFilter(req, resp);
			return;
		}
	}

	public void destroy() {

	}

}
