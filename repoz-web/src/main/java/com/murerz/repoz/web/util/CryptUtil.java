package com.murerz.repoz.web.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptUtil {

	public static String encodeBase64(byte[] bytes) {
		return new Base64(-1, null, true).encodeToString(bytes);
	}

	public static String encodeBase64Normal(byte[] bytes) {
		return new Base64(-1, null, false).encodeToString(bytes);
	}

	public static byte[] decodeBase64(String str) {
		return Base64.decodeBase64(str);
	}

	public static String encodeBase64String(String value, String charset) {
		try {
			return encodeBase64(value.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decodeBase64String(String value, String charset) {
		try {
			byte[] bytes = decodeBase64(value);
			return new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String sha256(String password) {
		try {
			String ret = DigestUtils.sha256Hex(password.getBytes("UTF-8"));
			return ret;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
