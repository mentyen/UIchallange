package com.cucumber.utilities;

import com.rebar.utilities.configprovider.exceptions.PropertyFileNotFoundException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigProvider {
	private static Properties props;
	private static Map<String, Properties> configMap = new HashMap();
	private static Logger logger = Logger.getLogger(ConfigProvider.class.getName());

	private ConfigProvider() {
	}

	private static Properties getInstance(String propertyFileName) {
		Properties props = null;
		if (configMap.size() == 0) {
			props = loadProperties(propertyFileName);
			configMap.put(propertyFileName, props);
			return props;
		} else {
			Iterator var2 = configMap.entrySet().iterator();

			while(var2.hasNext()) {
				Map.Entry entry = (Map.Entry)var2.next();
				if (((String)entry.getKey()).equals(propertyFileName)) {
					return (Properties)entry.getValue();
				}
			}

			props = loadProperties(propertyFileName);
			configMap.put(propertyFileName, props);
			return props;
		}
	}

	private static Properties getInstance() {
		if (props == null) {
			props = loadProperties();
			return props;
		} else {
			return props;
		}
	}

	private static Properties loadProperties() {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		InputStream in;
		try {
			in = loader.getClass().getResourceAsStream("/properties/NextGen.properties");
			props.load(in);
		} catch (NullPointerException var7) {
			System.out.println("NextGen.properties file not found ..searching again");

			try {
				InputStream inputStreamNextgen = ConfigProvider.class.getResourceAsStream("/properties/NextGen.properties");
				props.load(inputStreamNextgen);
				System.out.println("NextGen.properties file  found");
			} catch (Exception var6) {
				var6.printStackTrace();
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}

		try {
			in = ConfigProvider.class.getResourceAsStream("/properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String resource;
			while((resource = br.readLine()) != null) {
				System.out.println("Properties file found:" + resource);
				InputStream is = ConfigProvider.class.getResourceAsStream("/properties/" + resource);
				props.load(is);
			}

			br.close();
		} catch (NullPointerException var9) {
			throw new PropertyFileNotFoundException("No properties file found inside 'properties' folder under src/test/resources. Please add all your properties files under mentioned folder(create folder if doesn't exist).");
		} catch (IOException var10) {
			var10.printStackTrace();
		}

		return props;
	}

	private static Properties loadProperties(String propertyFile) {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = ConfigProvider.class.getResourceAsStream("/properties/" + propertyFile + ".properties");

		try {
			props.load(is);
		} catch (NullPointerException var5) {
			throw new PropertyFileNotFoundException("'" + propertyFile + ".properties' file not found. Please verify mentioned file should be present under 'properties' folder((create folder if doesn't exist)) in src/test/resources.");
		} catch (IOException var6) {
			logger.warning("Not able to load property!!");
		}

		return props;
	}

	public static String getAsString(String key) {
		return getInstance().getProperty(key);
	}

	public static int getAsInt(String key) {
		return Integer.parseInt(getInstance().getProperty(key));
	}

	public static String getAsString(String fileName, String key) {
		return getInstance(fileName).getProperty(key);
	}

	public static int getAsInt(String fileName, String key) {
		return Integer.parseInt(getInstance(fileName).getProperty(key));
	}

	public static String getAsString(String environment, String propertyFile, String key) {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = ConfigProvider.class.getResourceAsStream("/properties" + File.separator + environment + "/" + propertyFile + ".properties");
		String value = null;

		try {
			props.load(inputStream);
			value = props.getProperty(key);
			props.clear();
			inputStream.close();
		} catch (IOException var8) {
			logger.log(Level.SEVERE, "an exception was thrown", var8);
		}

		return value;
	}
}