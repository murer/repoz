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
import com.murerz.repoz.web.util.ServletUtil;

public class AuthGoogleFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthGoogleFilter.class);

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

		// https://accounts.google.com/o/oauth2/auth?scope=openid+email&redirect_uri=http%3A%2F%2Flocalhost:8080%2Fs%2FOauth2%2Fcallback&response_type=code&client_id=797755358727-2cu9c5l79uq97sudh62bb3uhl4b24nhc.apps.googleusercontent.com&access_type=offline
		ServletUtil.sendRedirect(resp, "https://accounts.google.com/o/oauth2/auth", "scope", "openid email", "redirect_uri", OAuth2GoogleServlet.GOOGLE_REDIRECT_URI,
				"response_type", "code", "client_id", OAuth2GoogleServlet.GOOGLE_CLIENT_ID, "access_type", "online");
	}

	public void destroy() {

	}

}
