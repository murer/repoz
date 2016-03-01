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

import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.CTX;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.UserPass;

public class AccessBasicFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (CTX.get("username") != null) {
			chain.doFilter(req, resp);
			return;
		}
		UserPass basic = ServletUtil.getBasicInfo(req);
		if (basic == null) {
			ServletUtil.sendUnauthorized(resp, req);
			return;
		}
		String pass = Config.me().password(basic.getUsername());
		if (pass == null || !pass.equals(basic.getPassword())) {
			ServletUtil.sendUnauthorized(resp, req);
			return;
		}
		CTX.set("username", basic.getUsername());
		chain.doFilter(req, resp);
	}

	public void destroy() {

	}

}
