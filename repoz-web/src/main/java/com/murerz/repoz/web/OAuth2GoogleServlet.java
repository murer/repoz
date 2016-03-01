package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.meta.GoogleOAuthToken;
import com.murerz.repoz.web.util.CryptUtil;
import com.murerz.repoz.web.util.GsonUtil;
import com.murerz.repoz.web.util.HttpClientUtil;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.SecurityHelper;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class OAuth2GoogleServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(OAuth2GoogleServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String error = ServletUtil.param(req, "error");
		if (error != null) {
			RepozUtil.sendHtmlError(resp, error);
			return;
		}

		String code = ServletUtil.param(req, "code");
		if (code == null) {
			throw new RuntimeException("code is required");
		}

		String clientId = Config.me().getGoogleClientId();
		String secret = Config.me().getGoogleSecret();
		String redirectURL = RepozUtil.getOauthCallback(req);
		GoogleOAuthToken oToken = HttpClientUtil.jsonPost(GoogleOAuthToken.class, "https://accounts.google.com/o/oauth2/token", "grant_type", "authorization_code", "code", code,
				"client_id", clientId, "client_secret", secret, "redirect_uri", redirectURL);
		String email = getEmail(oToken);
		LOG.info("User: " + email);
		if (email == null) {
			ServletUtil.sendJSRedirect(resp, "index.html");
			return;
		}
		String token = GsonUtil.createObject("u", email, "t", System.currentTimeMillis()).toString();
		token = CryptUtil.encodeBase64String(token, "UTF-8");
		token = SecurityHelper.me().sign(token);
		ServletUtil.addCookie(resp, "Repoz", token, "/", -1);
		ServletUtil.sendJSRedirect(resp, "panel.html");
	}

	private String getEmail(GoogleOAuthToken oToken) {
		try {
			Request req = Request.Get("https://www.googleapis.com/plus/v1/people/me");
			req.addHeader("Authorization", oToken.toAuth());
			HttpResponse resp = req.execute().returnResponse();
			HttpEntity entity = resp.getEntity();
			String str = Util.str(EntityUtils.toString(entity));
			int code = resp.getStatusLine().getStatusCode();
			if (code != 200) {
				throw new RuntimeException("error: " + code + "  " + str);
			}
			JsonObject json = GsonUtil.parse(str).getAsJsonObject();
			JsonArray array = json.get("emails").getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject o = element.getAsJsonObject();
				String value = o.get("value").getAsString();
				String type = o.get("type").getAsString();
				if ("account".equals(type) && value.endsWith("dextra-sw.com")) {
					return value;
				}
			}
			return null;
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
