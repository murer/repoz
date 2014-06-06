package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.Response;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MemoryFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.FileAccessManager;
import com.murerz.repoz.web.meta.GrantAccessManager;
import com.murerz.repoz.web.util.CryptUtil;

public class FileAccessRepoTest extends AbstractTestCase {

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, MemoryFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, FileAccessManager.class.getName());
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
		a.success("POST", "/access", "application/json;charset=UTF-8", "path=/&user=admin&pass=admin1");
		assertEquals(new Integer(403), execute(null, "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());

		assertEquals(new Integer(403), execute("o:a", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("o:a", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		a.success("POST", "/access", "application/json;charset=UTF-8", "path=/a/b&user=o&pass=a");
		assertEquals(new Integer(403), execute("o:a", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("o:a", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());

	}

	private Response execute(String basic, String method, String uri, String contentType, String text) {
		Response ret = s.execute(Request.create(method, uri).contentType(contentType).content(text));
		if (basic != null) {
			basic = CryptUtil.encodeBase64String(basic, "UTF-8");
			ret.headers().add("Authorization", basic);
		}
		return ret;
	}

}
