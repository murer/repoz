package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.Response;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MemoryFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.FileAccessManager;
import com.murerz.repoz.web.util.CryptUtil;

public class FileAccessRepoTest extends AbstractTestCase {

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, MemoryFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, FileAccessManager.class.getName());
		
		FileSystemFactory.create().deleteAll();
	}

	@Override
	public void tearDown() {
		System.getProperties().remove(FileSystemFactory.PROPERTY);
		System.getProperties().remove(AccessManagerFactory.PROPERTY);
		super.tearDown();
	}

	@Test
	public void testAccess() {
		assertEquals(new Integer(403), execute(null, "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		a.success("POST", "/access", "application/x-www-form-urlencoded;charset=UTF-8", "path=/&user=admin&pass=admin1&type=write");
		assertEquals(new Integer(403), execute(null, "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/c/d/e/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "GET", "/r/a/b/c/d/e/file.txt", null, null).code());

		assertEquals(new Integer(403), execute("o:a", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("o:a", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		a.success("POST", "/access", "application/x-www-form-urlencoded;charset=UTF-8", "path=/a/b&user=ooo&pass=aaaaaa&type=read");
		assertEquals(new Integer(403), execute("ooo:aaaaaa", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("ooo:aaaaaa", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("ooo:aaaaaa", "GET", "/r/file.txt", null, null).code());
		assertEquals(new Integer(200), execute("ooo:aaaaaa", "GET", "/r/a/b/file.txt", null, null).code());
		assertEquals(new Integer(200), execute("ooo:aaaaaa", "GET", "/r/a/b/c/d/e/file.txt", null, null).code());

	}

	private Response execute(String basic, String method, String uri, String contentType, String text) {
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

}
