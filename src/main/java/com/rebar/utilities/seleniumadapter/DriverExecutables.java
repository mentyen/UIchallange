package com.rebar.utilities.seleniumadapter;

import com.rebar.utilities.configprovider.ConfigProvider;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverExecutables {
    private static String chromeDriverExe = "./drivers/chromedriver_2.%s.exe";
    private static String ieDriverExe = "./drivers/IEDriverServer_%sbit_3.12.exe";
    private static String edgeDriverExe = "./drivers/msedgedriver.exe";
    private static String geckoDriverExe = "./drivers/geckodriver_%sbit_v0.20.1.exe";
    private static String safariDriverExe = "/usr/bin/safaridriver";
    private static String phantomJSexe = "./drivers/phantomjs_2.1.1.exe";
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    public static String proxyURL;
    private static String chromeVersion;
    private static String ieVersion;
    private static String ieDriverVersion;
    private static String firefoxVersion;
    private static String ieDriverType;
    private static String edgeDriverType;
    private static String edgeVersion;

    public DriverExecutables() {
    }

    protected static void setChromeDriverExe(int version) {
        chromeDriverExe = String.format(chromeDriverExe, version);
    }

    public static String getChromeDriverExe() {
        return chromeDriverExe;
    }

    protected static String getsafariDriverExe() {
        return safariDriverExe;
    }

    protected static void setIEDriverExe(int version) {
        ieDriverExe = String.format(ieDriverExe, version);
    }

    protected static String getIeDriverExe() {
        return ieDriverExe;
    }

    protected static String getEdgeDriverExe() {
        return edgeDriverExe;
    }

    protected static void setGeckoDriverExe(int version) {
        geckoDriverExe = String.format(geckoDriverExe, version);
    }

    protected static String getGeckoDriverExe() {
        return geckoDriverExe;
    }

    protected static String getPhantomjsExe() {
        return phantomJSexe;
    }

    protected static void setBrowserExe() {
        String jdkVersion = System.getProperty("sun.arch.data.model");
        String browserName = System.getProperty("browser", ConfigProvider.getAsString("browser"));
        if (browserName.equalsIgnoreCase("chrome")) {
            if (!chromeVersion.isEmpty()) {
                WebDriverManager.chromedriver().browserVersion(chromeVersion).proxy(proxyURL).setup();
            } else {
                //WebDriverManager.chromedriver().proxy(proxyURL).setup();
                WebDriverManager.chromedriver().setup();
            }
        } else if (browserName.equalsIgnoreCase("firefox")) {
            if (jdkVersion.equals("32")) {
                if (!firefoxVersion.isEmpty()) {
                    WebDriverManager.firefoxdriver().browserVersion(firefoxVersion).proxy(proxyURL).arch32().setup();
                } else {
                    WebDriverManager.firefoxdriver().proxy(proxyURL).arch32().setup();
                }
            } else if (!firefoxVersion.isEmpty()) {
                WebDriverManager.firefoxdriver().browserVersion(firefoxVersion).proxy(proxyURL).arch64().setup();
            } else {
                WebDriverManager.firefoxdriver().proxy(proxyURL).arch64().setup();
            }
        } else if (!browserName.equalsIgnoreCase("ie") && !browserName.equalsIgnoreCase("internetexplorer")) {
            if (browserName.equalsIgnoreCase("edge")) {
                if (edgeDriverType.equalsIgnoreCase("32")) {
                    if (!edgeVersion.isEmpty()) {
                        WebDriverManager.edgedriver().browserVersion(edgeVersion).arch32().proxy(proxyURL).setup();
                    } else {
                        WebDriverManager.edgedriver().arch32().proxy(proxyURL).setup();
                    }
                } else if (!edgeVersion.isEmpty()) {
                    WebDriverManager.edgedriver().browserVersion(edgeVersion).proxy(proxyURL).setup();
                } else {
                    WebDriverManager.edgedriver().proxy(proxyURL).setup();
                }
            } else if (browserName.equalsIgnoreCase("safari")) {
                System.setProperty("webdriver.safari.driver", safariDriverExe);
            }
        } else if (ieDriverType.equalsIgnoreCase("32")) {
            if (!ieVersion.isEmpty()) {
                WebDriverManager.iedriver().browserVersion(ieVersion).arch32().proxy(proxyURL).setup();
            } else if (ieDriverVersion != null && !ieDriverVersion.isEmpty()) {
                WebDriverManager.iedriver().driverVersion(ieDriverVersion).arch32().proxy(proxyURL).setup();
            } else {
                WebDriverManager.iedriver().arch32().proxy(proxyURL).setup();
            }
        } else if (!ieVersion.isEmpty()) {
            WebDriverManager.iedriver().browserVersion(ieVersion).proxy(proxyURL).setup();
        } else if (!ieDriverVersion.isEmpty() && ieDriverVersion != null) {
            WebDriverManager.iedriver().driverVersion(ieDriverVersion).arch32().proxy(proxyURL).setup();
        } else {
            WebDriverManager.iedriver().proxy(proxyURL).setup();
        }

    }

    static {
        String var10000 = ConfigProvider.getAsString("proxy.url");
        proxyURL = var10000 + ":" + ConfigProvider.getAsString("proxy.port");
        chromeVersion = System.getProperty("chrome.version", ConfigProvider.getAsString("chrome.version"));
        ieVersion = System.getProperty("ie.version", ConfigProvider.getAsString("ie.version"));
        ieDriverVersion = System.getProperty("ie.driver.version", ConfigProvider.getAsString("ie.driver.version"));
        firefoxVersion = System.getProperty("firefox.version", ConfigProvider.getAsString("firefox.version"));
        ieDriverType = System.getProperty("ie.driver", ConfigProvider.getAsString("ie.driver"));
        edgeDriverType = System.getProperty("edge.driver", ConfigProvider.getAsString("edge.driver"));
        edgeVersion = System.getProperty("edge.version", ConfigProvider.getAsString("edge.version"));
    }
}
