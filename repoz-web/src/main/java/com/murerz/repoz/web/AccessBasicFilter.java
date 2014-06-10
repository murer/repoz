package com.murerz.repoz.web;

import java.io.IOException;
import java.util.Properties;

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

import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.UserPass;
import com.murerz.repoz.web.util.Util;

public class AccessBasicFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AccessBasicFilter.class);

	private String user;
	private String pass;

	public void init(FilterConfig filterConfig) throws ServletException {
		String userpass = Util.str(System.getProperty("repoz.access.userpass"));
		if(userpass != null) {
			String[] array = userpass.split(":");
			user = array[0];
			pass = array[1];
			LOG.info("repoz.access.userpass: " + user);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
		Properties props = Util.properties(getClass().getClassLoader().getResource("repoz.access.properties"));
		if (props == null) {
			props = new Properties();
		}
		if (user != null && pass != null) {
			props.put(user, pass);
		}
		UserPass basic = ServletUtil.getBasicInfo(req);
		if (basic == null) {
			ServletUtil.sendUnauthorized(resp, req);
			return;
		}
		String pass = props.getProperty(basic.getUsername());
		if (pass == null || !pass.equals(basic.getPassword())) {
			ServletUtil.sendUnauthorized(resp, req);
			return;
		}
		chain.doFilter(req, resp);
	}

	public void destroy() {

	}

}