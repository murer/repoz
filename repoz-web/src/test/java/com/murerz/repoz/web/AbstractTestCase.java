package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.commons.http.HttpClientRequestService;
import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.RequestAdapter;
import com.googlecode.mycontainer.commons.http.Response;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.util.CryptUtil;

public class AbstractTestCase {

	protected MycontainerHelper mycontainer;
	protected int port;
	protected HttpClientRequestService s;
	protected RequestAdapter a;
	protected int maxLimit;

	@Before
	public void setUp() {
		maxLimit = 300;
		System.setProperty("repoz.post.maxLength", Integer.toString(maxLimit));
		System.setProperty("repoz.google.auth.disabled", "true");

		new File("target/access.log").delete();

		mycontainer = MycontainerHelper.me();
		mycontainer.setUp();
		port = mycontainer.bind(0);

		s = new HttpClientRequestService("http://localhost:" + port);
		a = new RequestAdapter(s);

	}

	@After
	public void tearDown() {
		FileSystemFactory.reset();
		Config.reset();
		if (mycontainer != null) {
			mycontainer.unbindPort(port);
			mycontainer.tearDown();
		}
	}

	public void assertResp(Request req, int code, String mediaType, String charset, String data) {
		Response resp = s.execute(req);
		assertEquals(new Integer(code), resp.code());
		assertEquals(mediaType, resp.content().mediaType());
		if (charset != null) {
			assertEquals(charset, resp.content().charset());
			assertEquals(data, resp.content().text());
		} else {
			assertNull(charset, resp.content().charset());
			assertEquals(data, new String(resp.content().data()));
		}

	}

	public Response execute(String basic, String method, String uri, String contentType, String text) {
		Request req = Request.create(method, uri);
		if (contentType != null) {
			req.contentType(contentType).content(text);
		}
		if (basic != null) {
			basic = CryptUtil.encodeBase64String(basic, "UTF-8");
			req.headers().add("Authorization", "Basic " + basic);
		}
		Response ret = s.execute(req);
		return ret;
	}

	public void setAccess(String querystring) {
		assertEquals(new Integer(200), execute("main:123", "POST", "/access", "application/x-www-form-urlencoded;charset=UTF-8", querystring).code());
	}

	public Response request(String method, String uri, String contentType, String content, String... headers) {
		Request req = Request.create(method, uri);
		for (int i = 0; i < headers.length; i += 2) {
			String key = headers[i];
			String value = headers[i + 1];
			req.header(key, value);
		}
		req.contentType(contentType).content(content);
		return s.execute(req);
	}
}
