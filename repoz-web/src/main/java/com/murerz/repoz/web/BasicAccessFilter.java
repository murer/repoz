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

import com.murerz.repoz.web.meta.AccessManager;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.UserPass;

public class BasicAccessFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
		try {
			String accessType = accessType(req, resp);
			if (accessType == null) {
				ServletUtil.sendMethodNotAllowed(req, resp);
				return;
			}
			String path = RepozUtil.path(req);
			UserPass auth = ServletUtil.getBasicInfo(req);

			if (path.endsWith("/" + RepozUtil.ACCESS)) {
				ServletUtil.sendForbidden(resp, req);
				return;
			}

			AccessManager am = AccessManagerFactory.create();
			if (auth != null && !am.authenticate(auth.getUsername(), auth.getPassword())) {
				ServletUtil.sendUnauthorized(resp, req);
				return;
			}

			if (!am.authorize(auth == null ? null : auth.getUsername(), path)) {
				ServletUtil.sendForbidden(resp, req);
				return;
			}

			chain.doFilter(req, resp);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

	}

	private String accessType(HttpServletRequest req, HttpServletResponse resp) {
		String method = req.getMethod();
		if ("GET".equals(method)) {
			return "read";
		}
		if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
			return "write";
		}
		return null;
	}

	public void destroy() {

	}

}
