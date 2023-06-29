package com.cucumber.stepdefinition;

import java.time.Duration;

import com.cucumber.CucumberRuntime;
import com.cucumber.pageobjectmanager.PageObjectManager;
import org.openqa.selenium.WebDriver;
import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.seleniumadapter.DriverManager;
import com.rebar.utilities.seleniumadapter.DriverManagerFactory;

public abstract class AbstractSteps {

    private static ThreadLocal<DriverManager> driverManager = new ThreadLocal<>();
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<PageObjectManager> pageObjectManager = new ThreadLocal<>();


    public void startDriver() {
        if (driverManager.get() == null)
        driverManager.set(DriverManagerFactory.getManager(ConfigProvider.getAsString("browser")));
        driver.set(driverManager.get().getDriver());
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigProvider.getAsInt("IMPLICIT_WAIT")));
        if (!ConfigProvider.getAsString("browser").equalsIgnoreCase("chrome"))
        driver.get().manage().window().maximize();
        pageObjectManager.set(new PageObjectManager(driver.get()));
         //CucumberRuntime.set(driver);
    }

    public void stopDriver() {
        if (driverManager.get() != null)
            driverManager.get().stopService();
        driverManager.set(null);
        driver.set(null);
        pageObjectManager.set(null);
    }

    public WebDriver getDriver() {
        // return CucumberRuntime.get().getDriver();
        return driver.get();
    }

    public PageObjectManager getPageObjectManager() {
        // return CucumberRuntime.get().getDriver();
        return pageObjectManager.get();
    }

}
