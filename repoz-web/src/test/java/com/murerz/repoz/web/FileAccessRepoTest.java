package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Response;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MemoryFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.FileAccessManager;
import com.murerz.repoz.web.util.RepozUtil;

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
		assertEquals(new Integer(401), execute(null, "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(401), execute(null, "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(401), execute("admin:admin1", "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(401), execute("admin:admin1", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		setAccess("path=/a&user=admin&pass=admin1&type=write");
		setAccess("path=/z&user=admin&pass=admin1&type=write");
		assertEquals(new Integer(401), execute(null, "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(401), execute(null, "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/c/d/e/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "GET", "/r/a/b/c/d/e/file.txt", null, null).code());
		assertEquals(new Integer(200), execute("admin:admin1", "DELETE", "/r/a/b/c/d/e/file.txt", null, null).code());

		assertEquals(new Integer(401), execute("o:a", "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(401), execute("o:a", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		setAccess("path=/a&user=ooo&pass=aaaaaa&type=read");
		assertEquals(new Integer(401), execute("ooo:aaaaaa", "PUT", "/r/z/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("ooo:aaaaaa", "PUT", "/r/a/b/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(403), execute("ooo:aaaaaa", "DELETE", "/r/a/b/file.txt", null, null).code());
		assertEquals(new Integer(401), execute("ooo:aaaaaa", "GET", "/r/z/file.txt", null, null).code());
		assertEquals(new Integer(200), execute("ooo:aaaaaa", "GET", "/r/a/b/file.txt", null, null).code());
		assertEquals(new Integer(404), execute("ooo:aaaaaa", "GET", "/r/a/b/c/d/e/file.txt", null, null).code());

		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/b/c/d/e/file.txt", "text/plain;charset=UTF-8", "a1").code());

	}

	@Test
	public void testCrud() {
		assertEquals("", listAccess("/a"));
		setAccess("path=/a&user=admin&pass=admin1&type=write");
		setAccess("path=/b&user=admin&pass=admin1&type=write");
		setAccess("path=/b&user=other&pass=other1&type=write");
		assertEquals("/a admin write 25f43b1486ad95a1398e3eeb3d83bc4010015fcc9bedb35b432e00298d5021f7", listAccess("/a"));
		assertEquals(
				"/b admin write 25f43b1486ad95a1398e3eeb3d83bc4010015fcc9bedb35b432e00298d5021f7\n/b other write 0ef821e6aa54508c6e15575b36aeac4b62521782712b00986297b9745b75790a",
				listAccess("/b"));
		deleteAccess("/b", "other");
		deleteAccess("/a", "admin");
		assertEquals("", listAccess("/a"));
		assertEquals("/b admin write 25f43b1486ad95a1398e3eeb3d83bc4010015fcc9bedb35b432e00298d5021f7", listAccess("/b"));
	}

	@Test
	public void testListRepository() {
		setAccess("path=/a&user=admin&pass=admin1&type=write");
		setAccess("path=/b&user=admin&pass=admin1&type=write");

		Response resp = execute("main:123", "GET", "/access?path=/", null, null);
		assertEquals(new Integer(200), resp.code());
		assertEquals("/a\n/b", resp.content().text().trim());
	}

	@Test
	public void testMetaFiles() {
		setAccess("path=/a&user=admin&pass=admin1&type=write");
		assertEquals(new Integer(200), execute("admin:admin1", "PUT", "/r/a/any/file.txt", "text/plain;charset=UTF-8", "a1").code());
		assertEquals(new Integer(200), execute("admin:admin1", "GET", "/r/a/any/file.txt", null, null).code());

		assertEquals(new Integer(403), execute("admin:admin1", "GET", "/r/a/" + RepozUtil.ACCESS, null, null).code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/a/" + RepozUtil.ACCESS, "text/plain;charset=UTF-8", "/a admin write admin1").code());

		assertEquals(new Integer(403), execute("admin:admin1", "GET", "/r/a/any/file.txt.repozmeta", null, null).code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/a/any/file.txt.repozmeta", "text/plain;charset=UTF-8", "/a admin write admin1").code());
		assertEquals(new Integer(403), execute("admin:admin1", "GET", "/r/a/any/other.txt.repozmeta", null, null).code());
		assertEquals(new Integer(403), execute("admin:admin1", "PUT", "/r/a/any/other.txt.repozmeta", "text/plain;charset=UTF-8", "/a admin write admin1").code());
	}

	private void deleteAccess(String path, String user) {
		assertEquals(new Integer(200), execute("main:123", "DELETE", "/access?path=" + path + "&user=" + user, null, null).code());
	}

	private String listAccess(String path) {
		Response resp = execute("main:123", "GET", "/access?path=" + path, null, null);
		assertEquals(new Integer(200), resp.code());
		String text = resp.content().text().trim();
		return text;
	}

}
