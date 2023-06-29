package com.api.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import com.cucumber.utilities.AssertionLibrary;
import com.rebar.utilities.Log;
import freemarker.log.Logger;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiGatewayTokenGenerator {

	private boolean initialized = false;
	private SSLConfig config = null;

	public ApiGatewayTokenGenerator(String keyStoreFileName, String keyStoreSecret) {
		init(keyStoreFileName, keyStoreSecret);
	}

	private void init(String keyStoreFileName, String keyStoreSecret) {
		try (FileInputStream keyStoreStream = new FileInputStream(new File(keyStoreFileName).getAbsolutePath());
				BufferedInputStream bis = new BufferedInputStream(keyStoreStream)) {

			// Create KeyStore
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(bis, keyStoreSecret.toCharArray());

			// Create SSL Context
			@SuppressWarnings("deprecation")
			org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory = new org.apache.http.conn.ssl.SSLSocketFactory(
					keyStore, keyStoreSecret);
			config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
			initialized = true;
		} catch (Exception e) {
			Log.warn("Fail to initialized Token Generator with KeyStore, as result exception accured:-->"
					+ e.getMessage());
		}
	}

	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

	public String getToken(String tokenApiUrl, String clientKey, String clientSecret) {
		if (!initialized) {
			Log.warn("Token Generator is not initialized with KeyStore. Run init first");
			return null;
		}

		if (empty(tokenApiUrl))
			AssertionLibrary.assertTrue(false,"tokenApiUrl should not be null, please check your data provider");
		if (empty(clientKey))
			AssertionLibrary.assertTrue(false,"clientKey should not be null, please check your data provider");
		if (empty(clientSecret))
			AssertionLibrary.assertTrue(false,"clientSecret should not be null, please check your data provider");

		try {
			// Generate Rest Client and Set the SSL Context in the properties
			RestAssured.useRelaxedHTTPSValidation();
			RestAssured.config = RestAssured.config().sslConfig(config);

			// Invoke Token API
			RequestSpecification httpRequest = RestAssured.given();
			httpRequest = httpRequest.auth().basic(clientKey, clientSecret)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.contentType("application/x-www-form-urlencoded").formParam("grant_type", "client_cert");

			// Get the response
			Response response = httpRequest.post(tokenApiUrl);

			// Verify status
			if (response.getStatusCode() != 200) {
				Log.error("Fail to get Token with tokenApiUrl,clientKey,clientSecret .Status Code :-->"
						+ response.getStatusCode() + " >>> error :--> "
						+ response.jsonPath().getString("error_description"));
				AssertionLibrary.assertTrue(false,"Fail to get Token with tokenApiUrl,clientKey,clientSecret . Get back with Status Code :-->"
						+ response.getStatusCode() + " >>> error :--> "
						+ response.jsonPath().getString("error_description"));
			}

			String token = response.jsonPath().getString("access_token");
			Log.info("Token:-->" + token);

			return token;

		} catch (Exception e) {
			AssertionLibrary.assertTrue(false,
					"Fail to get Token Generator with tokenApiUrl,clientKey,clientSecret as result exception accured:-->"
							+ e.getMessage());
		}
		return null;
	}

}
