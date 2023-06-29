package com.rebar.utilities.seleniumadapter;

import com.rebar.utilities.seleniumadapter.drivers.ChromeDriverManager;

public class DriverManagerFactory {
    public DriverManagerFactory() {
    }

    public static DriverManager getManager(String browserName) {
        Object driverManager = null;
        if (browserName.equalsIgnoreCase("chrome")) {
            driverManager = new ChromeDriverManager();
        } else if (browserName.equalsIgnoreCase("firefox")) {
            //driverManager = new FirefoxDriverManager();
        } else if (browserName.equalsIgnoreCase("phantomjs")) {
            //driverManager = new PhantomJSDriverManager();
        } else if (browserName.equalsIgnoreCase("safari")) {
            //driverManager = new SafariDriverManager();
        } else if (browserName.equalsIgnoreCase("edge")) {
            //driverManager = new EdgeDriverManager();
        } else {
            //driverManager = new InternetExplorerDriverManager();
        }

        return (DriverManager)driverManager;
    }
}
