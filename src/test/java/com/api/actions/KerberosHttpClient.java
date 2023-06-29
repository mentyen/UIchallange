package com.api.actions;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import javax.net.ssl.SSLContext;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import com.rebar.utilities.Log;
import com.cucumber.utilities.AssertionLibrary;
import com.cucumber.utilities.AssertionLibrary.Screenshot;
import com.rebar.utilities.extentreports.ExtentTestManager;
import java.util.Set;

public class KerberosHttpClient {
	private String principal;
	private String keyTabLocation;

	public KerberosHttpClient() {
	}

	public KerberosHttpClient(String principal, String keyTabLocation) {
		super();
		this.principal = principal;
		this.keyTabLocation = keyTabLocation;
	}

	public KerberosHttpClient(String principal, String keyTabLocation, String krb5Location) {
		this(principal, keyTabLocation);
		System.setProperty("java.security.krb5.conf", krb5Location);
	}

	public KerberosHttpClient(String principal, String keyTabLocation, boolean isDebug) {
		this(principal, keyTabLocation);
		if (isDebug) {
			System.setProperty("sun.security.spnego.debug", "true");
			System.setProperty("sun.security.krb5.debug", "true");
		}
	}

	public KerberosHttpClient(String principal, String keyTabLocation, String krb5Location, boolean isDebug) {
		this(principal, keyTabLocation, isDebug);
		System.setProperty("java.security.krb5.conf", krb5Location);
	}

	private static HttpClient buildSpengoHttpClient()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder builder = HttpClientBuilder.create();
		Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
				.register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
		builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
			@Override
			public Principal getUserPrincipal() {
				return null;
			}

			@Override
			public String getPassword() {
				return null;
			}
		});
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}
		};
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
		builder.setDefaultCredentialsProvider(credentialsProvider);
		CloseableHttpClient httpClient = builder.setSSLSocketFactory(csf).build();
		return httpClient;
	}

	public HttpResponse callRestUrl(final String url, final String userId) {
		// keyTabLocation = keyTabLocation.substring("file://".length());
		/*
		 * System.out.println( String.format("Calling KerberosHttpClient %s %s %s",
		 * this.principal, this.keyTabLocation, url));
		 */
		Configuration config = new Configuration() {
			@SuppressWarnings("serial")
			public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
				return new AppConfigurationEntry[] {
						new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
								AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
									{
										put("useTicketCache", "false");
										put("useKeyTab", "true");
										put("keyTab", keyTabLocation);
										// Krb5 in GSS API needs to be refreshed
										// so it does not throw the error
										// Specified version of key is not
										// available
										put("refreshKrb5Config", "true");
										put("principal", principal);
										put("storeKey", "true");
										put("doNotPrompt", "true");
										put("isInitiator", "true");
										put("debug", "false");
									}
								}) };
			}
		};
		Set<Principal> princ = new HashSet<Principal>(1);
		princ.add(new KerberosPrincipal(userId));
		Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
		try {
			LoginContext lc = new LoginContext("", sub, null, config);
			lc.login();
			Subject serviceSubject = lc.getSubject();
			return Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
				HttpResponse httpResponse = null;

				@Override
				public HttpResponse run() {
					try {
						HttpUriRequest request = new HttpGet(url);
						HttpClient spnegoHttpClient = buildSpengoHttpClient();
						httpResponse = spnegoHttpClient.execute(request);
						return httpResponse;
					} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException ioe) {
						ioe.printStackTrace();
					}
					return httpResponse;
				}
			});
		} catch (Exception le) {
			le.printStackTrace();
			;
		}
		return null;
	}

	/**
	 * will check if string is not null or empty
	 */
	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

	/**
	 * will log line in to report
	 * 
	 */

	public void logFail(Object msg) {
		Log.info(String.valueOf(msg));
		ExtentTestManager.getTest().fail(String.valueOf(msg));
	}

	public String getToken(String principals, String keytabLocations, String krbconf, String krb2jwt) {
		if (empty(principals))
			AssertionLibrary.assertTrue(false, "principals should not be null, please check your data provider",
					Screenshot.NOT_REQUIRED);
		if (empty(keytabLocations))
			AssertionLibrary.assertTrue(false, "keytabLocations should not be null, please check your data provider",
					Screenshot.NOT_REQUIRED);
		if (empty(krbconf))
			AssertionLibrary.assertTrue(false, "krbconf should not be null, please check your data provider",
					Screenshot.NOT_REQUIRED);
		if (empty(krb2jwt))
			AssertionLibrary.assertTrue(false, "krb2jwt should not be null, please check your data provider",
					Screenshot.NOT_REQUIRED);	

		KerberosHttpClient restTest = new KerberosHttpClient(principals, keytabLocations, krbconf, false);
		HttpResponse response = restTest.callRestUrl(krb2jwt, principals);
		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (UnsupportedOperationException | IOException e) {			
			logFail("Fail to get response content,following exceptions accured:-->" + e.getMessage());
			Assert.fail();
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			logFail("Excpected status code:--> 200  Actual status code:-->" + response.getStatusLine().getStatusCode());
			Assert.fail();
		}
		
		String token = null;
		try {
			token = new String(IOUtils.toByteArray(is), "UTF-8");			
		} catch (IOException e) {
			logFail("Fail to get token,following exceptions accured:-->" + e.getMessage());
			Assert.fail();
		}
		return token;
	}
}