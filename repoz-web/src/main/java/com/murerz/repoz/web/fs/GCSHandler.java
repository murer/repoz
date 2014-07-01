package com.murerz.repoz.web.fs;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.murerz.repoz.web.meta.Config;

public class GCSHandler {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final Object MUTEX = new Object();

	private static GCSHandler me = null;

	private HttpRequestFactory factory;

	public static GCSHandler me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					GCSHandler x = new GCSHandler();
					x.prepare();
					me = x;
				}
			}
		}
		return me;
	}

	private void prepare() {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream("gcs.p12");
			if (stream == null) {
				throw new RuntimeException("private key gcs.p12 not found");
			}
			PrivateKey key = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), stream, "notasecret", "privatekey", "notasecret");

			NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY).setServiceAccountId(email())
					.setServiceAccountScopes(Arrays.asList("https://www.googleapis.com/auth/devstorage.full_control", "https://www.googleapis.com/auth/devstorage.read_write"))
					.setServiceAccountPrivateKey(key).build();
			factory = httpTransport.createRequestFactory(credential);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String email() {
		return Config.me().reqProperty("repoz.gcs.auth.email");
	}

	public HttpRequestFactory getFactory() {
		return factory;
	}

	public GenericUrl createURL(String path) {
		String bucket = Config.me().reqProperty("repoz.gcs.bucket");
		String base = Config.me().getGoogleCloudStorageBase();
		if (base.length() > 0) {
			base = "/" + base;
		}
		return new GenericUrl("https://" + bucket + ".storage.googleapis.com" + base + path);
	}

	public GenericUrl createListURL(String query) {
		String bucket = Config.me().reqProperty("repoz.gcs.bucket");
		return new GenericUrl("https://" + bucket + ".storage.googleapis.com" + query);
	}

}
