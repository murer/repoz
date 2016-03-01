package com.murerz.repoz.web;

import com.googlecode.mycontainer.commons.http.Request;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MemoryFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.Config;
import com.murerz.repoz.web.meta.GrantAccessManager;

public class MemoryRepoTest extends AbstractFileSystemTestCase {

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, MemoryFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, GrantAccessManager.class.getName());
		Config.reset();
		
		FileSystemFactory.create().deleteAll();
	}

	@Override
	public void testListFiles() {
		super.testListFiles();
		assertResp(Request.create("GET", "/r/a/docs?l=true&r=true"), 200, "text/plain", "UTF-8", "/a/docs/file.txt\n/a/docs/some\n/a/docs/some/dir\n/a/docs/some/dir/other.txt\n");
		assertResp(Request.create("GET", "/r/a/config?l=true&r=true"), 200, "text/plain", "UTF-8", "/a/config/x.txt\n");
		assertResp(Request.create("GET", "/r/a/config/x.txt?l=true&r=true"), 200, "text/plain", "UTF-8", "");
		assertResp(Request.create("GET", "/r/a?l=true&r=true"), 200, "text/plain", "UTF-8",
				"/a/config\n/a/config/x.txt\n/a/docs\n/a/docs/file.txt\n/a/docs/some\n/a/docs/some/dir\n/a/docs/some/dir/other.txt\n/a/other.txt\n");
		assertResp(Request.create("GET", "/r/?l=true&r=true"), 200, "text/plain", "UTF-8",
				"/a\n/a/config\n/a/config/x.txt\n/a/docs\n/a/docs/file.txt\n/a/docs/some\n/a/docs/some/dir\n/a/docs/some/dir/other.txt\n/a/other.txt\n/b\n/b/other.txt\n");
	}

	@Override
	public void tearDown() {
		System.getProperties().remove(FileSystemFactory.PROPERTY);
		System.getProperties().remove(AccessManagerFactory.PROPERTY);
		super.tearDown();
	}

}
