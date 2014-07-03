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
import com.murerz.repoz.web.meta.User;
import com.murerz.repoz.web.util.CTX;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.UserPass;

public class AccessRepoFilter implements Filter {

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
			if (path.endsWith(RepozUtil.ACCESS) || path.endsWith(RepozUtil.REPOZMETA)) {
				ServletUtil.sendForbidden(resp, req);
				return;
			}

			UserPass auth = ServletUtil.getBasicInfo(req);

			AccessManager am = AccessManagerFactory.create();
			User user = new User().setPath(path).setType(accessType);
			if (auth != null) {
				user.setUser(auth.getUsername()).setPass(auth.getPassword());
			}
			int code = am.auth(user);
			if (code == 401) {
				ServletUtil.sendUnauthorized(resp, req);
				return;
			}
			if (code == 403) {
				ServletUtil.sendForbidden(resp, req);
				return;
			}
			if (code != 200) {
				throw new RuntimeException("unexpected: " + code);
			}
			if (auth != null) {
				CTX.set("username", auth.getUsername());
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
