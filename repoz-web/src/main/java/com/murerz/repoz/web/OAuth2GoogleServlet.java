package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.meta.GoogleOAuthToken;
import com.murerz.repoz.web.util.HttpClientUtil;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;

public class OAuth2GoogleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String GOOGLE_CLIENT_SECRET = "m5ZNLzYoFoYXfKNip6j994aI";
	public static final String GOOGLE_CLIENT_ID = "797755358727-2cu9c5l79uq97sudh62bb3uhl4b24nhc.apps.googleusercontent.com";
	public static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/repoz/oauth2google";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// authuser 0
		// code 4/WJo-9o0pgbQ1SZV3fQVrE3xBPE66.grdbZdt9X_wT3oEBd8DOtNCbYAhJjQI
		// hd dextra-sw.com
		// num_sessions 1
		// prompt consent
		// session_state 83dc0374464c36a0dda18c16eed0f367b4c58ca8..c54b

		// error=access_denied

		String error = ServletUtil.param(req, "error");
		if (error != null) {
			RepozUtil.sendHtmlError(resp, error);
			return;
		}

		String code = ServletUtil.param(req, "code");
		if (code == null) {
			throw new RuntimeException("code is required");
		}

		// code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
		// client_id=8819981768.apps.googleusercontent.com&
		// client_secret={client_secret}&
		// redirect_uri=https://oauth2-login-demo.appspot.com/code&
		// grant_type=authorization_code
		GoogleOAuthToken oToken = HttpClientUtil.jsonPost(GoogleOAuthToken.class, "https://accounts.google.com/o/oauth2/token", "grant_type", "authorization_code", "code", code,
				"client_id", GOOGLE_CLIENT_ID, "client_secret", GOOGLE_CLIENT_SECRET, "redirect_uri", GOOGLE_REDIRECT_URI);
		System.out.println("oToken: " + oToken);
	}
}
