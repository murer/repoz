package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.Response;

public class RepoTest extends AbstractTestCase {

	@Test
	public void testRepo() {
		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other.txt"));

		assertEquals(new Integer(200), a.code("PUT", "/r/file.txt", "text/plain;charset=UTF-8", "my text file"));
		assertEquals(new Integer(200), a.code("PUT", "/r/with/some/dir/other.txt", "text/plain;charset=UTF-8", "my other file"));

		assertResp(Request.create("GET", "/r/file.txt"), 200, "text/plain", "UTF-8", "my text file");
		assertResp(Request.create("GET", "/r/with/some/dir/other.txt"), 200, "text/plain", "UTF-8", "my other file");

		assertEquals(new Integer(200), a.code("DELETE", "/r/file.txt"));
		assertEquals(new Integer(200), a.code("DELETE", "/r/with/some/dir/other.txt"));

		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other.txt"));
	}

	private void assertResp(Request req, int code, String mediaType, String charset, String data) {
		Response resp = s.execute(req);
		assertEquals(new Integer(code), resp.code());
		assertEquals(mediaType, resp.content().mediaType());
		assertEquals(charset, resp.content().charset());
		assertEquals(data, resp.content().text());
	}

	@Test
	public void testMediaType() {
		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other"));

		assertEquals(new Integer(200), s.execute(Request.create("PUT", "/r/file.txt").contentType("application/octet-stream").content("my text file".getBytes())).code());
		assertEquals(new Integer(200), a.code("PUT", "/r/with/some/dir/other", "text/plain;charset=UTF-8", "my other file"));

		assertResp(Request.create("GET", "/r/file.txt"), 200, "text/plain", "UTF-8", "my text file");
		assertResp(Request.create("GET", "/r/with/some/dir/other"), 200, "text/plain", "UTF-8", "my other file");

		assertEquals(new Integer(200), a.code("DELETE", "/r/file.txt"));
		assertEquals(new Integer(200), a.code("DELETE", "/r/with/some/dir/other"));

		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other"));
	}

}
