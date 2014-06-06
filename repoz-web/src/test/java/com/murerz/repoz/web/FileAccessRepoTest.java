package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Request;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MemoryFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.FileAccessManager;

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
	public void testRepo() {
		assertEquals(new Integer(200), a.code("PUT", "/r/file.txt", "text/plain;charset=UTF-8", "my text file"));
		assertEquals(new Integer(200), a.code("PUT", "/r/dir/other.txt", "text/plain;charset=UTF-8", "my other file"));

		assertResp(Request.create("GET", "/r/file.txt"), 200, "text/plain", "UTF-8", "my text file");
		assertResp(Request.create("GET", "/r/with/some/dir/other.txt"), 200, "text/plain", "UTF-8", "my other file");

		assertEquals(new Integer(200), a.code("DELETE", "/r/file.txt"));
		assertEquals(new Integer(200), a.code("DELETE", "/r/with/some/dir/other.txt"));

		assertEquals(new Integer(404), a.code("GET", "/r/file.txt"));
		assertEquals(new Integer(404), a.code("GET", "/r/with/some/dir/other.txt"));
	}

}
