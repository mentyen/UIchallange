package com.rebar.utilities.seleniumadapter;

import com.rebar.utilities.configprovider.ConfigProvider;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class DriverManager {
    protected WebDriver driver;
    private static Logger logger = LogManager.getLogger(DriverManager.class.getName());

    public DriverManager() {
    }

    protected abstract void startService();

    protected abstract void createDriver();

    public void stopService() {
    }

    public WebDriver getDriver() {
        if (null == this.driver) {
            if (!this.isSeleniumGridRequired()) {
                DriverExecutables.setBrowserExe();
                this.startService();
            }
            this.createDriver();
        }
        return this.driver;
    }

    protected boolean isSeleniumGridRequired() {
        String value = System.getProperty("SeleniumGrid", ConfigProvider.getAsString("SeleniumGrid"));
        return Boolean.valueOf(value);
    }

    protected Platform getPlatform() {
        String platformValue = System.getProperty("platform", ConfigProvider.getAsString("platform"));
        if (!platformValue.equalsIgnoreCase("Windows7") && !platformValue.equalsIgnoreCase("windows") && !platformValue.equalsIgnoreCase("7")) {
            if (!platformValue.equalsIgnoreCase("windows8") && !platformValue.equalsIgnoreCase("8")) {
                if (!platformValue.equalsIgnoreCase("windows8.1") && !platformValue.equalsIgnoreCase("8.1")) {
                    if (!platformValue.equalsIgnoreCase("windows10") && !platformValue.equalsIgnoreCase("10")) {
                        if (!platformValue.equalsIgnoreCase("windowsXP") && !platformValue.equalsIgnoreCase("xp")) {
                            if (platformValue.equalsIgnoreCase("mac")) {
                                return Platform.MAC;
                            } else if (platformValue.equalsIgnoreCase("vista")) {
                                return Platform.VISTA;
                            } else if (platformValue.equalsIgnoreCase("linux")) {
                                return Platform.LINUX;
                            } else {
                                return platformValue.equalsIgnoreCase("unix") ? Platform.UNIX : Platform.ANY;
                            }
                        } else {
                            return Platform.XP;
                        }
                    } else {
                        return Platform.WIN10;
                    }
                } else {
                    return Platform.WIN8_1;
                }
            } else {
                return Platform.WIN8;
            }
        } else {
            return Platform.WINDOWS;
        }
    }

    public URL getServerUrl() {
        URL url = null;
        String urlString = "";

        try {
            urlString = System.getProperty("hub_url", ConfigProvider.getAsString("hub_url")).trim();
            url = this.toURL(urlString);
        } catch (NullPointerException var4) {
            logger.warn("hub_url property is not defned.");
        }

        if (urlString.isEmpty()) {
            logger.warn("hub_url value is not defned.");
        }

        return url;
    }

    private URL toURL(String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException var4) {
            logger.warn("url may not be correct: " + url);
        }

        return url;
    }
}

