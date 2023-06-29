package com.rebar.utilities.seleniumadapter.drivers;

import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.seleniumadapter.DriverExecutables;
import com.rebar.utilities.seleniumadapter.DriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ChromeDriverManager extends DriverManager {
    private ChromeDriverService chService;
    private static Logger logger = Logger.getLogger(ChromeDriverManager.class.getName());
    private static String chromeVersion = System.getProperty("chrome.version", ConfigProvider.getAsString("chrome.version"));
    private static String headLessFlag = System.getProperty("headless", ConfigProvider.getAsString("headless"));
    private String chromecaps = System.getProperty("chrome.caps.list.of.strings", ConfigProvider.getAsString("chrome.caps.list.of.strings"));
    private String downloadPath = System.getProperty("chrome.file.download.path", ConfigProvider.getAsString("chrome.file.download.path"));
    private String autoFileDownload = System.getProperty("auto.file.download", ConfigProvider.getAsString("auto.file.download"));

    public ChromeDriverManager() {
    }

    public void startService() {
        String driverExePath = DriverExecutables.getChromeDriverExe();
        if (!this.isServiceInitialized()) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (!chromeVersion.isEmpty()) {
                try {
                    File file = new File(loader.getResource(driverExePath).getFile());
                    this.chService = (new ChromeDriverService.Builder()).usingDriverExecutable(file).usingAnyFreePort().build();
                } catch (NullPointerException var5) {
                    logger.info("Chrome Driver exe not found. Using default exe file from server.");
                }
            }
            try {
                this.chService = ChromeDriverService.createDefaultService();
                this.chService.start();
            } catch (IOException var4) {
                logger.warning("Chrome service couldn't start!!!");
            }
        }

    }

    private boolean isServiceInitialized() {
        return null != this.chService;
    }

    public void stopService() {
        if (this.isServiceInitialized() && this.chService.isRunning()) {
            this.chService.stop();
        } else {
            this.driver.quit();
        }

    }

    public void createDriver() {
        ChromeOptions options = new ChromeOptions();
        String[] values;
        if (!this.chromecaps.isEmpty() && this.chromecaps.contains("||")) {
            values = this.chromecaps.split(Pattern.quote("||"));

            for(int i = 0; i < values.length; ++i) {
                String[] caps = values[i].split(",");
                if (!caps[1].equalsIgnoreCase("true") && !caps[1].equalsIgnoreCase("false")) {
                    options.setCapability(caps[0], caps[1]);
                } else {
                    boolean flag = Boolean.valueOf(caps[1]);
                    options.setCapability(caps[0], flag);
                }
            }
        } else if (!this.chromecaps.isEmpty()) {
            values = this.chromecaps.split(",");
            if (!values[1].equalsIgnoreCase("true") && !values[1].equalsIgnoreCase("false")) {
                options.setCapability(values[0], values[1]);
            } else {
                boolean flag = Boolean.valueOf(values[1]);
                options.setCapability(values[0], flag);
            }
        }

        HashMap chromePrefs;
        if (headLessFlag.equalsIgnoreCase("true")) {
            values = ConfigProvider.getAsString("headless.options.list.of.strings").split(",");
            options.addArguments(values);
            options.setCapability("platformName", this.getPlatform());
            options.setCapability("browserVersion", chromeVersion);
            if (org.apache.commons.lang3.StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})) {
                chromePrefs = new HashMap();
                chromePrefs.put("download.default_directory", this.downloadPath);
            }

            if (this.isSeleniumGridRequired()) {
                this.driver = new RemoteWebDriver(this.getServerUrl(), options);
            } else {
                System.out.println("Selenium Grid flag " + this.isSeleniumGridRequired());
                this.driver = new ChromeDriver(options);
            }
        } else {
            values = ConfigProvider.getAsString("options.list.of.strings").split(",");
            options.addArguments(values);
            chromePrefs = new HashMap();
            if (!this.autoFileDownload.isEmpty() && this.autoFileDownload.equalsIgnoreCase("true")) {
                chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
            } else {
                chromePrefs.put("profile.default_content_settings.popups", 0);
            }

            chromePrefs.put("safebrowsing.enabled", "true");//NEED TO CHECK ON THIS
            if (StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})) {
                chromePrefs.put("download.default_directory", this.downloadPath);
            }

            options.setExperimentalOption("prefs", chromePrefs);
            options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));//ADDED
            options.setCapability("platformName", this.getPlatform());
           // options.setCapability("browserVersion", chromeVersion);
            //enabling logger
           // LoggingPreferences logPrefs = new LoggingPreferences();
           // logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
            //options.setCapability( "goog:loggingPrefs", logPrefs );
            if (this.isSeleniumGridRequired()) {
                this.driver = new RemoteWebDriver(this.getServerUrl(), options);
            } else {
                this.driver = new ChromeDriver(this.chService, options);
            }
        }

    }
}
