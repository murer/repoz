package com.murerz.repoz.web;

import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.GCSFileSystem;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.GrantAccessManager;

public class GCSRepoTestManual extends AbstractFileSystemTestCase {

	@Override
	public void setUp() {
		super.setUp();
		System.setProperty(FileSystemFactory.PROPERTY, GCSFileSystem.class.getName());
		System.setProperty(AccessManagerFactory.PROPERTY, GrantAccessManager.class.getName());

		FileSystemFactory.create().deleteAll();
	}

	@Override
	public void tearDown() {
		System.getProperties().remove(FileSystemFactory.PROPERTY);
		System.getProperties().remove(AccessManagerFactory.PROPERTY);
		super.tearDown();
	}

}
