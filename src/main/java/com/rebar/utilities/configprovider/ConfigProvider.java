package com.rebar.utilities.configprovider;

import com.rebar.utilities.configprovider.exceptions.PropertyFileNotFoundException;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class ConfigProvider {
	private static Properties props;
	private static Map<String, Properties> configMap = new HashMap();
	private static Logger logger = LogManager.getLogger(ConfigProvider.class.getName());

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
private final static String propertiesSourcePath="/properties/NextGen.properties";
	private static Properties loadProperties() {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream in;

//		try {
//			in = ConfigProvider.class.getResourceAsStream(propertiesSourcePath);
//			props.load(in);
//			logger.info("Reading properties file:" + propertiesSourcePath);
//		} catch (NullPointerException var7) {
//			logger.warn(propertiesSourcePath+"  file not found .. retry");
//			try {
//				InputStream inputStreamNextgen = ConfigProvider.class.getResourceAsStream(propertiesSourcePath);
//				props.load(inputStreamNextgen);
//				logger.info("Reading properties file:" + propertiesSourcePath);
//			} catch (Exception var6) {
//				var6.printStackTrace();
//			}
//		} catch (Exception var8) {
//			var8.printStackTrace();
//		}

		try {
			in = ConfigProvider.class.getResourceAsStream("/properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String resource;
			while((resource = br.readLine()) != null) {
				logger.info("Reading properties : " + resource);
				InputStream is = ConfigProvider.class.getResourceAsStream("/properties/" + resource);
				props.load(is);
			}

			br.close();
		} catch (NullPointerException var9) {
			throw new PropertyFileNotFoundException("No properties file found inside 'properties' folder under src/test/resources. Please add all your properties files under mentioned folder(create folder if doesn't exist).");
		} catch (IOException var10) {
			logger.error(var10.getMessage());
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
			logger.warn("Not able to load property!!");
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
			logger.error("Exception in getAsString() : ", var8.getMessage());
		}

		return value;
	}
}