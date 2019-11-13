package com.sample.test.demo;

import static org.testng.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private Properties configProperties;



    public Configuration() {
        loadProperties();
    }

    private void loadProperties() {
        configProperties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assertTrue(classLoader != null);
        InputStream inputStream = classLoader.getResourceAsStream(CONFIG_FILE_NAME);
        try {
            configProperties.load(inputStream);
        } catch (final IOException e) {
        }
    }

    public String getBrowser() {
        return getProperty("browser");
    }

    public String getPlatform() {
        return getProperty("platform");
    }

    public String getUrl() {
        return getProperty("url");
    }
    public String getProperty(String propertyName) {
        return configProperties.getProperty(propertyName);
    }
}
