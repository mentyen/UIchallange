package com.api.actions;

import com.rebar.utilities.configprovider.ConfigProvider;

public class TokenGeneratorJWT {

	public String getToken() {
		KerberosHttpClient client =new KerberosHttpClient();
		String keytabLocations = System.getProperty("user.dir")+ ConfigProvider.getAsString("keytabLocations");
		String principals = ConfigProvider.getAsString("principals");
		String krbconf = System.getProperty("user.dir")+ConfigProvider.getAsString("krbconf");
		String krb2jwt = ConfigProvider.getAsString("krb2jwt");
		String token=client.getToken(principals, keytabLocations, krbconf,krb2jwt);
		token=token.substring(10, token.length()-2);		
		return token;
	}

}
