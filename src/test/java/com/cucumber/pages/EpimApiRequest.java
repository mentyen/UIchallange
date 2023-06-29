package com.cucumber.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.api.actions.autocomplete.Address;
import com.api.actions.autocomplete.Root;
import com.api.actions.autocomplete.Telecom;
import com.cucumber.utilities.GenericMethods;
import com.rebar.utilities.configprovider.ConfigProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;

import static com.cucumber.utilities.GenericMethods.isEmpty;

public class EpimApiRequest {
	Response response = null;
	private String url = ConfigProvider.getAsString("url"),
			userName = ConfigProvider.getAsString("uName"),
			userSecret = ConfigProvider.getAsString("secret");
	private static Logger Log = LogManager.getLogger();


	private String getBearerToken() {
		try {
			response = RestAssured.given().auth().preemptive().basic(userName, userSecret)
					.headers("Content-type", "application/x-www-form-urlencoded")
					.formParam("grant_type", "client_credentials").when().post(url).then().extract().response();
			if (response.getStatusCode() != 200) {			
				Assert.fail(getValueFromResponse(response, "message"));
			}
		} catch (Exception e) {		
			Assert.fail("Exception in getToken():==>" + e.getLocalizedMessage());
		}
		return getValueFromResponse(response, "access_token");
	}

	private void getResponse(String url, String endpoint, String token) {
		try {
			RestAssured.baseURI = url;
			response = RestAssured.given().headers("Token", token).when().get(endpoint).then().extract().response();

			if (response.getStatusCode() != 200) {		
				Assert.fail("Status :" + response.getStatusCode() + " " + getValueFromResponse(response, "error"));
			}
		} catch (Exception e) {		
			Assert.fail("Exception in getResponse():==>" + e.getLocalizedMessage());
		}
	}

	public List<String> getResponseAsList(String url, String endpoint) {
		getResponse(url, endpoint, getBearerToken());
		return getValueFromResponseAsList();
	}

	private static String getValueFromResponse(Response response, String string) {
		Map<String, Object> deserializedResponse = response.as(new TypeRef<Map<String, Object>>() {
		});
		return (String) deserializedResponse.get(string);
	}

	private List<String> getValueFromResponseAsList() {
		List<String> deserializedResponse = null;
		if (response.getBody().asString().startsWith("[")) {
			deserializedResponse = response.as(new TypeRef<List<String>>() {
			});
		} else {
			Log.warn("json is not an instance of JSONArray");
			deserializedResponse = new ArrayList<>();
		}
		return deserializedResponse;
	}

	////////////////////////////// UNIT TEST////////////////////////////////////////
	private void get(String url, String endpoint, String token) {
		try {
			RestAssured.baseURI = url;
			response = RestAssured.given().headers("Token", token).when().get(endpoint).then().extract().response();
			// Log all details of the response
			response.then().log().all();
		} catch (Exception e) {
			Log.warn("Exception in getCall():==>" + e.getLocalizedMessage());
			Assert.fail("Exception in getCall():==>" + e.getLocalizedMessage());
		}
	}

	public void setStatus(String searchType, String endPoint) {
		String baseUrl = null;
		try {
			baseUrl = "url";
		} catch (Exception e) {
			Log.warn("Fail to read property file, exception :->" + e.getMessage());
			Assert.fail("Fail to read property file, exception :->" + e.getMessage());
		}
		get(baseUrl, endPoint, getBearerToken());
		setRunTimeProperty("statusCode", String.valueOf(response.getStatusCode()));
		setRunTimeProperty("endpoint", baseUrl + endPoint);
	}

	public void verifyStatusCode(int expectedStatusCode) {
		if(expectedStatusCode==response.getStatusCode()){
			Log.info("Status code : " + expectedStatusCode);
		}else{
			Assert.fail("Expected :"+expectedStatusCode+"\nActual :"+response.getStatusCode()+"\nBody :"+response.getBody().asString()+"\nEndpoint :"+getRunTimeProperty("endpoint"));
		}
	}

	public void verifyTime(int expectedTime) {
		if (response.getTime() > expectedTime) {
			Log.warn("Expected Time: is: " + expectedTime + ", but Actual Time: is: " + response.getTime());
			Assert.fail("Expected Time: is: " + expectedTime + ", but Actual Time: is: " + response.getTime());
		}
	}

	public void verifyBody(String body) {
		switch (body) {
		case "empty":
			if (getValueFromResponseAsList().size() > 0) {
				Log.warn(
						"Expected Response body should be empty, Actual response body is :==>" + response.asString());
				Assert.fail(
						"Expected Response body should be empty, Actual response body is :==>" + response.asString());
			}
			break;
		case "notEmpty":
			if (getValueFromResponseAsList().size() <= 0) {
				Log.warn("Expected Response body should not be empty, Actual response body is :==>"
						+ response.asString());
				Assert.fail("Expected Response body should not be empty, Actual response body is :==>"
						+ response.asString());
			}
			break;
		}

	}

	public void verifyAutocompleteBody(String body) {
		String expected = "{\"orgName\":\"ATO PHARMACY INC\",\"tin\":null,\"address\":[{\"city\":\"Burbank\",\"state\":\"CA\",\"district\":\"Los Angeles\",\"postalCode\":\"915022707\",\"line\":[\"401 S Glenoaks Blvd Ste 102\"],\"telecom\":[{\"use\":\"work\",\"value\":\"8185587895\",\"system\":\"phone\"},{\"use\":\"work\",\"value\":\"8185587897\",\"system\":\"fax\"}]}]}";
		Gson gson = new GsonBuilder().create();
		Root root = null;
		switch (body) {
		case "empty":
			root = gson.fromJson(response.getBody().asString(), Root.class);
			if (isEmpty(root.getOrgName()) && root.getAddress() == null) {
				Log.info("Expected values ==> 'null' , Actual values ==> " + response.getBody().asString());
			} else {
				String address = null;
				if (root.getAddress() != null) {
					address = root.getAddress().get(0).getCity();
				}
				Log.warn("Expected 'orgName': null, Actual 'orgName':" + root.getOrgName()
						+ "\n Expected 'address': null, Actual 'address':" + address);
				Assert.fail("Expected 'orgName': null, Actual 'orgName':" + root.getOrgName()
						+ "\n Expected 'address': null, Actual 'address':" + address);
			}
			break;
		case "notEmpty":
			if (!response.getBody().asString().equalsIgnoreCase(expected)) {
				Log.warn("Expected body :==>" + expected + "\n Actual body:==>" + response.getBody().asString(),false);
				root = gson.fromJson(response.getBody().asString(), Root.class);
				// can implement detailed validation here
				// Log.info(root.getOrgName());
				// Log.info(root.getAddress().get(0).getCity());
				Assert.fail("Expected body :==>" + expected + "\n Actual body:==>" + response.getBody().asString());
				break;
			} else {
				Log.info("Expected body :==>" + expected + "\n Actual body:==>" + response.getBody().asString());
			}

		}

	}

	public String getPhone() {
		Gson gson = new GsonBuilder().create();
		Root root = null;
		root = gson.fromJson(response.getBody().asString(), Root.class);
		List<Telecom> obj = root.getAddress().get(0).getTelecom();
		for (Telecom tel : obj) {
			if (tel.getSystem().contains("phone")) {
				return tel.getValue();
			}
		}
		return null;
	}
	public String getStreet() {
		Gson gson = new GsonBuilder().create();
		Root root = null;
		root = gson.fromJson(response.getBody().asString(), Root.class);
		List<Address> addresses= root.getAddress();
		List<String> lines=new ArrayList<>();
		if(addresses==null){
			Assert.fail("After calling the endpoint, EPIM does not return any address : "+response.getBody().asString()+" : "+getRunTimeProperty("endpoint"));
		}else{
			for(Address address:addresses){
				List<String> obj = address.getLine();
				for (String street : obj) {
					lines.add("Street : "+street);
					if (Character.isDigit(street.charAt(0))) {
						return street;
					}
				}
			}
			Assert.fail("Unable to select a valid street from the addresses returned by EPIM \n" + String.join("\n", lines));
		}
		return null;
	}

}
