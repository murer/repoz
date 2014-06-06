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
	}

	@Override
	public void tearDown() {
		System.getProperties().remove(FileSystemFactory.PROPERTY);
		System.getProperties().remove(AccessManagerFactory.PROPERTY);
		super.tearDown();
	}

	@Test
	public void testAccess() {
		System.setProperty(AccessManagerFactory.PROPERTY, FileAccessManager.class.getName());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/dir/a/file.txt", "text/plain;charset=UTF-8", "a2").code());
		assertEquals(new Integer(403), execute(null, "PUT", "/r/dir/b/file.txt", "text/plain;charset=UTF-8", "a3").code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/file.txt", "text/plain;charset=UTF-8", "a4").code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/dir/a/file.txt", "text/plain;charset=UTF-8", "a5").code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/dir/b/file.txt", "text/plain;charset=UTF-8", "a6").code());

		System.setProperty(AccessManagerFactory.PROPERTY, GrantAccessManager.class.getName());
		assertEquals(new Integer(200), execute(null, "PUT", "/r/.repoz-access.txt", "text/plain;charset=UTF-8", "admin:admin1 rw\nuser1:user1 ro\n").code());
		assertEquals(new Integer(200), execute(null, "PUT", "/r/dir/.repoz-access.txt", "text/plain;charset=UTF-8", "user2:user2 ro\n").code());
		assertEquals(new Integer(200), execute(null, "PUT", "/r/dir/a/.repoz-access.txt", "text/plain;charset=UTF-8", "anonymous:x ro\n").code());

		System.setProperty(AccessManagerFactory.PROPERTY, FileAccessManager.class.getName());
		assertEquals(new Integer(200), execute(null, "GET", "/r/file.txt", "text/plain;charset=UTF-8", "a1").code());
		
		assertEquals(new Integer(200), a.code("PUT", "/r/file.txt", "text/plain;charset=UTF-8", "my text file"));
		assertEquals(new Integer(200), a.code("PUT", "/r/dir/other.txt", "text/plain;charset=UTF-8", "my other file"));

		assertResp(Request.create("GET", "/r/file.txt"), 200, "text/plain", "UTF-8", "my text file");
		assertResp(Request.create("GET", "/r/with/some/dir/other.txt"), 200, "text/plain", "UTF-8", "my other file");

		assertEquals(new Integer(200), a.code("DELETE", "/r/file.txt"));
		assertEquals(new Integer(200), a.code("DELETE", "/r/with/some/dir/other.txt"));

		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other.txt"));
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
