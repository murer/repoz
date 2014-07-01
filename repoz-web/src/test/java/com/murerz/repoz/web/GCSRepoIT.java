package com.murerz.repoz.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.http.Request;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.GCSFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.GrantAccessManager;
import com.murerz.repoz.web.util.Util;

public class GCSRepoIT extends AbstractFileSystemTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(GCSRepoIT.class);

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, GCSFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, GrantAccessManager.class.getName());
		System.setProperty("repoz.gcs.base", "test-" + Util.randomPositiveLong());

		FileSystemFactory.create().deleteAll();
	}

	@Override
	public void testListFiles() {
		super.testListFiles();
		assertResp(Request.create("GET", "/r/a/docs?l=true&r=true"), 200, "text/plain", "UTF-8", "/a/docs/file.txt\n/a/docs/some/dir/other.txt\n");
		assertResp(Request.create("GET", "/r/a/config?l=true&r=true"), 200, "text/plain", "UTF-8", "/a/config/x.txt\n");
		assertResp(Request.create("GET", "/r/a/config/x.txt?l=true&r=true"), 200, "text/plain", "UTF-8", "");
		assertResp(Request.create("GET", "/r/a?l=true&r=true"), 200, "text/plain", "UTF-8", "/a/config/x.txt\n/a/docs/file.txt\n/a/docs/some/dir/other.txt\n/a/other.txt\n");
		assertResp(Request.create("GET", "/r/?l=true&r=true"), 200, "text/plain", "UTF-8",
				"/a/config/x.txt\n/a/docs/file.txt\n/a/docs/some/dir/other.txt\n/a/other.txt\n/b/other.txt\n");
	}

	@Override
	public void tearDown() {
		try {
			FileSystemFactory.create().deleteAll();

			System.getProperties().remove("repoz.gcs.base");
			System.getProperties().remove(FileSystemFactory.PROPERTY);
			System.getProperties().remove(AccessManagerFactory.PROPERTY);
			super.tearDown();
		} catch (Exception e) {
			LOG.error("error tearDown", e);
		}
	}

}
