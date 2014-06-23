package com.murerz.repoz.web.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.murerz.repoz.web.AbstractTestCase;
import com.murerz.repoz.web.fs.GCSHandler;

public class GCSHandlerIT extends AbstractTestCase {

	@Test
	public void testLicense() throws Exception {
		HttpRequestFactory factory = GCSHandler.me().getFactory();
		String bucket = Config.me().getProperty("repoz.gcs.bucket");
		assertNotNull(bucket);
		HttpRequest req = factory.buildGetRequest(new GenericUrl("https://storage.googleapis.com/" + bucket));
		HttpResponse resp = req.execute();
		assertEquals("application/xml; charset=UTF-8", resp.getContentType());
		String str = resp.parseAsString();
		System.out.println(str);
	}
}
