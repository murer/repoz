package com.murerz.repoz.web.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
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
import com.murerz.repoz.web.util.CryptUtil;

public class GCSHandler {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final Object MUTEX = new Object();

	private static GCSHandler me = null;

	private HttpRequestFactory factory;

	private PrivateKey key;

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
			key = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), stream, "notasecret",
					"privatekey", "notasecret");
			System.out.println("X: " + email());
			NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
					.setJsonFactory(JSON_FACTORY).setServiceAccountId(email())
					.setServiceAccountScopes(Arrays.asList("https://www.googleapis.com/auth/devstorage.full_control",
							"https://www.googleapis.com/auth/devstorage.read_write"))
					.setServiceAccountPrivateKey(key).build();
			factory = httpTransport.createRequestFactory(credential);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String email() {
		return Config.me().reqProperty("repoz.gcs.auth.email");
	}

	public HttpRequestFactory getFactory() {
		return factory;
	}

	public GenericUrl createURL(String path) {
		String fullPath = createFullPath(path);
		return new GenericUrl("https://storage.googleapis.com" + fullPath);
	}

	private String createFullPath(String path) {
		String bucket = getBucket();
		String base = Config.me().getGoogleCloudStorageBase();
		if (base.length() > 0) {
			base = "/" + base;
		}
		String fullPath = "/" + bucket + base + path;
		return fullPath;
	}

	public String getBucket() {
		return Config.me().reqProperty("repoz.gcs.bucket");
	}

	public GenericUrl createListURL(String query) {
		String bucket = getBucket();
		return new GenericUrl("https://" + bucket + ".storage.googleapis.com" + query);
	}

	public String sign(String data) {
		try {
			Signature signer = Signature.getInstance("SHA256withRSA");
			signer.initSign(key);
			signer.update(data.getBytes("UTF-8"));
			byte[] sign = signer.sign();
			return CryptUtil.encodeBase64Normal(sign);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String createSignedURL(String path, long expires) {
		try {
			String fullPath = createFullPath(path);
			StringBuilder content = new StringBuilder();
			content.append("GET\n\n\n");
			content.append(expires).append("\n");
			content.append(fullPath);
			String sign = sign(content.toString());
			GenericUrl url = createURL(path);
			StringBuilder ret = new StringBuilder();
			ret.append(url);
			ret.append("?GoogleAccessId=").append(URLEncoder.encode(email(), "UTF-8"));
			ret.append("&Expires=").append(expires);
			ret.append("&Signature=").append(URLEncoder.encode(sign, "UTF-8"));
			return ret.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
