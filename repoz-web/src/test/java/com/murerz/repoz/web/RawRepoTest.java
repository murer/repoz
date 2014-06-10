package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.Request;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RawFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.GrantAccessManager;

public class RawRepoTest extends AbstractFileSystemTestCase {

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, RawFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, GrantAccessManager.class.getName());

		FileSystemFactory.create().deleteAll();
	}

	@Override
	public void tearDown() {
		System.getProperties().remove(FileSystemFactory.PROPERTY);
		System.getProperties().remove(AccessManagerFactory.PROPERTY);
		super.tearDown();
	}
	
	@Test
	public void testMeta() {
		assertEquals(new Integer(200), a.code("PUT", "/r/file.txt", "text/plain;charset=UTF-8", "my text file"));
		assertResp(Request.create("GET", "/r/file.txt"), 200, "text/plain", "UTF-8", "my text file");
		assertEquals(new Integer(404), a.code("GET", "/r/file.txt.repozmeta"));
	}

}
