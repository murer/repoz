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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.murerz.repoz.web.meta.GoogleOAuthToken;
import com.murerz.repoz.web.util.GsonUtil;
import com.murerz.repoz.web.util.HttpClientUtil;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class OAuth2GoogleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String GOOGLE_CLIENT_SECRET = "m5ZNLzYoFoYXfKNip6j994aI";
	public static final String GOOGLE_CLIENT_ID = "797755358727-2cu9c5l79uq97sudh62bb3uhl4b24nhc.apps.googleusercontent.com";
	public static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/repoz/oauth2google";

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

		GoogleOAuthToken oToken = HttpClientUtil.jsonPost(GoogleOAuthToken.class, "https://accounts.google.com/o/oauth2/token", "grant_type", "authorization_code", "code", code,
				"client_id", GOOGLE_CLIENT_ID, "client_secret", GOOGLE_CLIENT_SECRET, "redirect_uri", GOOGLE_REDIRECT_URI);

		String email = getEmail(oToken);
		System.out.println("s: " + email);
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
