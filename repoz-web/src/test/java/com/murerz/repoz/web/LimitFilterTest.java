package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.murerz.repoz.web.util.Util;

public class LimitFilterTest extends AbstractTestCase {

	@Test
	public void testLimit() {
		String data = Util.generateString("a", maxLimit);
		assertEquals(new Integer(405), a.code("POST", "/bla", "text/plain;charset=UTF-8", data));
		assertEquals(new Integer(413), a.code("POST", "/bla", "text/plain;charset=UTF-8", data + "a"));
		assertEquals(new Integer(405), a.code("PUT", "/bla", "text/plain;charset=UTF-8", data));
		assertEquals(new Integer(413), a.code("PUT", "/bla", "text/plain;charset=UTF-8", data + "a"));
	}

}
