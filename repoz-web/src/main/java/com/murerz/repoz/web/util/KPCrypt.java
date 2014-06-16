package com.murerz.repoz.web.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class KPCrypt {

	private static final String ALG_SIGNATURE = "SHA1withRSA";

	private static final String STRING_ENCODE = "utf-8";

	private static final String ALG_KEY = "RSA";

	private PrivateKey privateKey;

	private PublicKey publicKey;

	private Signature signature;

	public static KPCrypt create() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALG_KEY);
			keyGen.initialize(1024);
			KeyPair keypair = keyGen.genKeyPair();
			KPCrypt ret = new KPCrypt();
			ret.privateKey = keypair.getPrivate();
			ret.publicKey = keypair.getPublic();
			return ret;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public String signB64UrlSafe(String data) {
		byte[] sign = sign(data);
		return CryptUtil.encodeBase64(sign);
	}

	public byte[] sign(String data) {
		try {
			return sign(data.getBytes(STRING_ENCODE));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] sign(byte[] bytes) {
		try {
			Signature sig = getSignature();
			sig.initSign(privateKey);
			sig.update(bytes, 0, bytes.length);
			return sig.sign();
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	private Signature getSignature() throws NoSuchAlgorithmException {
		if (signature == null) {
			signature = Signature.getInstance(ALG_SIGNATURE);
		}
		return signature;
	}

	public boolean verify(String text, String sign) {
		return verify(text, CryptUtil.decodeBase64(sign));
	}

	private boolean verify(String text, byte[] sign) {
		try {
			return verify(text.getBytes(STRING_ENCODE), sign);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean verify(byte[] data, byte[] sign) {
		try {
			Signature sig = Signature.getInstance(ALG_SIGNATURE);
			sig.initVerify(publicKey);
			sig.update(data, 0, data.length);
			return sig.verify(sign);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public static KPCrypt create(PrivateKey privateKey, PublicKey publicKey) {
		KPCrypt ret = new KPCrypt();
		ret.privateKey = privateKey;
		ret.publicKey = publicKey;
		return ret;
	}

	public static KPCrypt create(byte[] privateKey, byte[] publicKey) {
		try {
			PrivateKey privateKeyObj = null;
			PublicKey publicKeyObj = null;
			KeyFactory factory = KeyFactory.getInstance(ALG_KEY);
			if (privateKey != null) {
				EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
				privateKeyObj = factory.generatePrivate(privateKeySpec);
			}
			if (publicKey != null) {
				EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
				publicKeyObj = factory.generatePublic(publicKeySpec);
			}
			return create(privateKeyObj, publicKeyObj);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPrivateKeyHex() {
		byte[] encoded = getPrivateKey().getEncoded();
		return Hex.encodeHexString(encoded);
	}

	public String getPublicKeyHex() {
		byte[] encoded = getPublicKey().getEncoded();
		return Hex.encodeHexString(encoded);
	}

	public static KPCrypt create(String privateKey, String publicKey) {
		try {
			byte[] privateKeyData = null;
			byte[] publicKeyData = null;
			if (privateKey != null) {
				privateKeyData = Hex.decodeHex(privateKey.toCharArray());
			}
			if (publicKey != null) {
				publicKeyData = Hex.decodeHex(publicKey.toCharArray());
			}
			return create(privateKeyData, publicKeyData);
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}
	}

	public JsonObject sign(JsonElement json) {
		if (json == null) {
			json = JsonNull.INSTANCE;
		}
		String sign = signB64UrlSafe(json.toString());
		return GsonUtil.createObject("s", sign, "c", json);
	}

	public boolean verify(JsonObject signed) {
		JsonElement sign = signed.get("s");
		JsonElement content = signed.get("c");
		if (sign == null || sign.isJsonNull()) {
			return false;
		}
		if (content == null) {
			content = JsonNull.INSTANCE;
		}
		return verify(content.toString(), sign.getAsString());
	}

	public static void main(String[] args) {
		Provider[] providers = Security.getProviders();
		for (Provider provider : providers) {
			System.out.println("Provider: " + provider);
			Set<Service> services = provider.getServices();
			for (Service service : services) {
				String alg = service.getAlgorithm();
				System.out.println("alg: " + alg);
			}
		}
	}

}