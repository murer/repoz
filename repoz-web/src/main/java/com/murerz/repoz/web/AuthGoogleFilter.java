package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.CTX;
import com.murerz.repoz.web.util.CryptUtil;
import com.murerz.repoz.web.util.GsonUtil;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.SecurityHelper;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class AuthGoogleFilter implements Filter {

	private static final long TIMEOUT = 1l * 60 * 60 * 1000;

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

		Cookie cookie = ServletUtil.cookie(req, "Repoz");
		JsonObject obj = validate(cookie);
		if (obj != null) {
			CTX.set("username", obj.get("u").getAsString());
			chain.doFilter(req, resp);
			return;
		}

		String clientId = Config.me().getGoogleClientId();

		String uri = ServletUtil.getURIWithoutContextPath(req);
		if ("/panel.html".equals(uri)) {
			ServletUtil.sendRedirect(resp, "https://accounts.google.com/o/oauth2/auth", "scope", "openid email", "redirect_uri", RepozUtil.getOauthCallback(req), "response_type",
					"code", "client_id", clientId, "access_type", "online");
			return;
		}

		ServletUtil.writeJson(resp, GsonUtil.createObject("error", "Forbidden"));
	}

	private JsonObject validate(Cookie cookie) {
		if (cookie == null) {
			return null;
		}
		String token = Util.str(cookie.getValue());
		if (token == null) {
			return null;
		}
		token = SecurityHelper.me().unsign(token);
		if (token == null) {
			return null;
		}
		token = CryptUtil.decodeBase64String(token, "UTF-8");
		JsonObject obj = GsonUtil.parse(token).getAsJsonObject();
		long t = obj.get("t").getAsLong();
		if (System.currentTimeMillis() <= (t + TIMEOUT)) {
			return obj;
		}
		return null;
	}

	public void destroy() {

	}

}
