package com.rebar.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rebar.utilities.configprovider.ConfigProvider;
import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.RestAssured;
//RestAssuredResponseImpl;
import com.jayway.restassured.internal.RestAssuredResponseImpl;

public class SMSUtil {

	private static Properties properties;

	public static void main(String[] args) throws JSONException, IOException, ParseException {
		//System.setProperty("env", "QA");
		// SMSUtil.downloadSecretFromSMS();
		//SMSUtil util = new SMSUtil();
		//URL url = util.test();
		//System.out.print(url.getFile());
		//System.out.println(util.downloadSecretFromSMS());
	}

	public static String downloadSecretFromSMS() {

		File srcFile = SMSUtil.returnFile("data/env.json");
		File jsonOutputpath = SMSUtil.returnFile("data/" + "envOut.json");

		String downloadPath = System.getProperty("downloadPath", ConfigProvider.getAsString("downloadPath"));
		JSONObject reqJson = new JSONObject();
		JSONObject retrieveReqJson = null;

		try {			

			FileUtils.copyFile(srcFile, jsonOutputpath);
			JSONParser parser = new JSONParser();
			String str_Request = FileUtils.readFileToString(jsonOutputpath, StandardCharsets.UTF_8);
			retrieveReqJson = (JSONObject) parser.parse(str_Request);
			reqJson.put("testSecretsDownloadRequest", retrieveReqJson);

			Log.info("REQUEST :-->" + reqJson.toString());
			RestAssured.baseURI = System.getProperty("sms.uri", ConfigProvider.getAsString("sms.uri"));// "https://eqeplatform-ct.dev.net";

			RestAssuredResponseImpl stat = (RestAssuredResponseImpl) RestAssured.given().relaxedHTTPSValidation()
					.header("Accept", "application/json").header("Content-Type", "application/json")
					.body(reqJson.toString()).

					when().post("/secrets/download").thenReturn().getBody();

			Log.info("STATUS :-->" + stat.getStatusCode());

			byte[] fileContents = stat.getBody().asByteArray();
			File file;

			if (downloadPath != null) {
				file = new File(downloadPath);
			}

			else {
				downloadPath = System.getProperty("user.dir") + File.separator + retrieveReqJson.get("path");
				file = new File(downloadPath);
			}
			if (file.exists()) {
				file.delete();
			}
			OutputStream out = new FileOutputStream(file);
			out.write(fileContents);
			out.close();
			downloadPath = file.getParent();

		} catch (Exception e) {
			Log.warn("EXCEPTION :-->"+e.getMessage());
		}
		return downloadPath;
	}

	public static void updateJsonParameters(List<String> allParam, String desFile) throws IOException {
		Map<String, String> keyMap = new HashMap<String, String>();
		keyMap.put("environment", allParam.get(0));
		keyMap.put("path", allParam.get(1));
		keyMap.put("jwtToken", allParam.get(2));
		String updatedJson = "";

		try {

			for (Map.Entry<String, String> entry : keyMap.entrySet()) {
				if (entry.getValue() != null) {
					updatedJson = updateJSONPathValue(desFile, entry.getKey(), entry.getValue());
					BufferedWriter writer = new BufferedWriter(new FileWriter(desFile));

					writer.write(updatedJson);
					writer.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String updateJSONPathValue(String jsonPath, String keyPath, String keyValue) throws IOException {

		String strJson = new String(Files.readAllBytes(Paths.get(jsonPath)), StandardCharsets.UTF_8);

		String paths = keyPath;
		String values = keyValue;

		String updatedJson = null;
		Configuration configuration = Configuration.builder().build();

		if (NumberUtils.isNumber(values)) {
			int value1 = Integer.parseInt(values);
			updatedJson = JsonPath.using(configuration).parse(strJson).set(paths, value1).jsonString();
		} else {
			updatedJson = JsonPath.using(configuration).parse(strJson).set(paths, values).jsonString();
		}

		System.out.println("updatedJson=" + updatedJson);
		return updatedJson;
	}

	public static File returnFile(String resource) {
		File file = null;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		URL res = loader.getResource(resource);
		if (res.getProtocol().equals("jar")) {
			try {
				InputStream input = loader.getClass().getResourceAsStream(resource);
				file = File.createTempFile("tempfile", ".tmp");
				OutputStream out = new FileOutputStream(file);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.close();
				file.deleteOnExit();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {

			file = new File(res.getFile());
		}

		if (file != null && !file.exists()) {
			throw new RuntimeException("Error: File " + file + " not found!");
		}
		return file;
	}

	public static void loadPropFromSMS(String propertyFilePath) throws Exception {
		File dir = new File(propertyFilePath);
		File[] files = dir.listFiles((dir1, name) -> name.endsWith(".properties"));
		for (File f : files) {
			Log.info("SMS downloaded properties path :--> " + f);
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(f));
				properties = new Properties();
				try {
					properties.load(reader);
					for (String name : properties.stringPropertyNames()) {
						String value = properties.getProperty(name);
						System.setProperty(name, value);
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException fne) {
				fne.printStackTrace();
				throw new RuntimeException("Configuration properties not found at " + propertyFilePath);
			}

			catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	public URL test() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("data/env.json").getFile());
		System.out.println(file.getAbsolutePath());

		URL rsc = classLoader.getResource("data/env.json");
		return rsc;
	}

}
