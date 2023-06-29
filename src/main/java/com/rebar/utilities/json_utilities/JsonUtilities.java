package com.rebar.utilities.json_utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.rebar.utilities.Log;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonUtilities {

	public static void main(String[] args) throws IOException {

		String expectedJsonPath = ".\\src\\test\\resources\\data\\test.json";
		String actualJsonPath = ".\\src\\test\\resources\\data\\test1.json";
		
		//String expectedJsonPath = "C:\\Users\\adcibtj\\workspace\\EQE_AST_MEGA_GF_Eagle_Gopi\\expected.json";
		//String actualJsonPath = "C:\\Users\\adcibtj\\workspace\\EQE_AST_MEGA_GF_Eagle_Gopi\\actual.json";
		CompareTwoJson(expectedJsonPath, actualJsonPath);
		//=============================================================
		String jsonPath = ".\\account-tolerances.json";
		String keyPath = "data[0].toleranceVal";
		
		String keyValue = GetKeyValueFromJson(jsonPath, keyPath);
		System.out.println("Key path: " + keyPath + " && Key value is : " + keyValue);
		//=============================================================
		jsonPath = ".\\src\\test\\resources\\data\\test.json";
		keyPath = "sequences[0].models[0].latestModelVersion";
		keyValue = "121212";
		
		try {
			String updatedJSON = updateJSONPathValue(jsonPath, keyPath, keyValue);
			System.out.println(updatedJSON);
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
		
	}

	private static String GetKeyValueFromJson(String jsonPath, String keyPath) {
		
		String jsonString;
		String keyValue = "Error: Key not found in JSON.";
		try {
			jsonString = new String(Files.readAllBytes(Paths.get(jsonPath)),StandardCharsets.UTF_8);
			keyValue= JsonPath.read(jsonString,"$." + keyPath).toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyValue;
	}

	public static String updateJSONPathValue(String jsonPath, String keyPath, String keyValue)
			throws Exception {
		
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
		
		Log.info("updatedJson=" + updatedJson);
		return updatedJson;
	}
	
	private static void CompareTwoJson(String expectedJsonPath, String actualJsonPath) throws IOException {
		
		String diffJsonRaw = null;
		FileWriter writer = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			String failureMessage = "Error: ";

			File expectedFile = new File(expectedJsonPath);
			File actualFile = new File(actualJsonPath);
			writer = new FileWriter("JsonDiff.txt", false);
			
			JsonNode expectedNode, actualNode;
			expectedNode = mapper.readTree(expectedFile);
			actualNode = mapper.readTree(actualFile);

			Boolean statusAfterCompare = actualNode.equals(expectedNode);

			if (statusAfterCompare) {
				Log.info("Both actual and expected JSON's are equal.");
				writer.write("Both actual and expected JSON's are equal\n");
				
			} else {
				Log.info("Actual and expected JSON's are Not equal.");
				JSONAssert.assertEquals(failureMessage, expectedNode.toString(), actualNode.toString(),
						JSONCompareMode.STRICT);
				
			}

		} catch (AssertionError e) {
			diffJsonRaw = e.getMessage();
			
			String replaceText = diffJsonRaw.replace("Error:  ", "");
			String[] diffRaw = replaceText.split(" ; ");
			int i = 0;
			writer.write("Total number of difference are: " + diffRaw.length+"\n");
			while(i < diffRaw.length) {
				writer.write("==============================================\n");
				writer.write("Key("+i+"): " + diffRaw[i]);
				writer.write("\n");
				i++;
			}
		
			
		}
		catch (JSONException e) {
			System.out.println(e.getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(" -- Comparison Done -- ");
			writer.close();
		}
	}
}