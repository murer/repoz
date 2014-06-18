package com.murerz.repoz.web;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GSCPoc {

	private static final String SERVICE_ACCOUNT_EMAIL = "797755358727-0vo50r71dcf96p0kmgl03o2uh3tbdeut@developer.gserviceaccount.com";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	@Test
	public void testGCS() throws Exception {
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY).setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
				.setServiceAccountScopes(Arrays.asList("https://www.googleapis.com/auth/devstorage.full_control", "https://www.googleapis.com/auth/devstorage.read_write"))
				.setServiceAccountPrivateKeyFromP12File(new File("key.p12")).build();

		// String URI = "https://storage.googleapis.com/repoz-test";
		String URI = "https://repoz-test.storage.googleapis.com/object";
		HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
		GenericUrl url = new GenericUrl(URI);
		HttpRequest request = requestFactory.buildGetRequest(url);
		request.setRequestMethod("PUT");
		request.setContent(new ByteArrayContent("text/plain", "test".getBytes()));
		HttpResponse response = request.execute();
		String content = response.parseAsString();
		System.out.println("x: " + content);

		URI = "https://repoz-test.storage.googleapis.com/object";
		requestFactory = httpTransport.createRequestFactory(credential);
		url = new GenericUrl(URI);
		request = requestFactory.buildGetRequest(url);
		request.setRequestMethod("GET");
		response = request.execute();
		content = response.parseAsString();
		System.out.println("x: " + content);

		URI = "https://repoz-test.storage.googleapis.com/object";
		requestFactory = httpTransport.createRequestFactory(credential);
		url = new GenericUrl(URI);
		request = requestFactory.buildGetRequest(url);
		request.setRequestMethod("DELETE");
		response = request.execute();
		content = response.parseAsString();
		System.out.println("x: " + content);

	}
}
