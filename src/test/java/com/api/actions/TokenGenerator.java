package com.api.actions;

import com.rebar.utilities.Log;
import com.rebar.utilities.configprovider.ConfigProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class TokenGenerator {

	private String KEYSTORE_SECRET_FOR_AUTH_CERT;
	private String AUTH_CERT;
	private SecretKeySpec keys;	

	public TokenGenerator(String keystore, String authcert) {
		if (keystore.length() > 0) {
			this.KEYSTORE_SECRET_FOR_AUTH_CERT = decrypt(keystore, getKey());
		} else {
			this.KEYSTORE_SECRET_FOR_AUTH_CERT = keystore;
		}

		this.AUTH_CERT = decrypt(authcert, getKey());
	}

	public String getToken(String apiType) {
		String clientKey;
		String clientSecret;
		ApiGatewayTokenGenerator generator = new ApiGatewayTokenGenerator(AUTH_CERT, KEYSTORE_SECRET_FOR_AUTH_CERT);
		String tokenApiUrl = decrypt(ConfigProvider.getAsString("TokenApiUrl"), getKey());
		if(apiType.equalsIgnoreCase("GCS")) {
			clientKey = 	decrypt(System.getProperty("GcsClientKey"), getKey());
			clientSecret = decrypt(System.getProperty("GcsClientSecret"), getKey());
		}
		else {
			clientKey = 	decrypt(System.getProperty("ClientKey"), getKey());
			clientSecret = decrypt(System.getProperty("ClientSecret"), getKey());
		}
		
		return generator.getToken(tokenApiUrl, clientKey, clientSecret);
	}

	private String decrypt(String string, SecretKeySpec key) {
		String iv = string.split(":")[0];
		String property = string.split(":")[1];
		String str = null;
		try {
			Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
			str = new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
		} catch (Exception e) {
			Log.warn("Fail to decript string, following exceptions accured:-->" + e.getMessage());
		}
		return str;

	}

	private static byte[] base64Decode(String property) throws IOException {
		return Base64.getDecoder().decode(property);
	}

	private SecretKeySpec getKey() {
		String password = "12345QWERTY";
		byte[] salt = new String("12345678").getBytes();
		int iterationCount = 40000;
		int keyLength = 128;
		try {
			keys = createSecretKey(password.toCharArray(), salt, iterationCount, keyLength);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return keys;
	}

	private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
		SecretKey keyTmp = keyFactory.generateSecret(keySpec);
		return new SecretKeySpec(keyTmp.getEncoded(), "AES");
	}

}
