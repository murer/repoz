package com.murerz.repoz.web;

import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.commons.http.HttpClientRequestService;
import com.googlecode.mycontainer.commons.http.RequestAdapter;

public class AbstractTestCase {

	protected MycontainerHelper mycontainer;
	protected int port;
	protected HttpClientRequestService s;
	protected RequestAdapter a;

	@Before
	public void setUp() {
		mycontainer = MycontainerHelper.me();
		mycontainer.setUp();
		port = mycontainer.bind(0);

		s = new HttpClientRequestService("http://localhost:" + port);
		a = new RequestAdapter(s);
	}

	@After
	public void tearDown() {
		if (mycontainer != null) {
			mycontainer.unbindPort(port);
			mycontainer.tearDown();
		}
	}
}
