package com.cucumber.utilities;


import com.cucumber.CucumberRuntime;
import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.extentreports.ExtentTestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.yaml.snakeyaml.Yaml;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GenericMethods {
    private static Logger Log = LogManager.getLogger();
    private com.cucumber.utilities.AssertionLibrary AssertionLibrary=null;
    private final JavascriptExecutor javascriptExecutor;
    private final int DEFAULT_IMPLICIT_WAIT = 0;
    private FluentWait<WebDriver> fluentWait;
    private WebDriver driver;
    private static String winID = "";
    protected Select select;
    private Duration pollingInterval = Duration.ofMillis(ConfigProvider.getAsInt("POLLING_INTERVAL"));
    private Duration fluentWaitDuration = Duration.ofSeconds(ConfigProvider.getAsInt("FLUENT_WAIT"));
    private static final String SET_INPUT = "Set input: ";
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    static SecretKeySpec keys;
    private static File[] files = null;


    public Properties runTimeProperties = new Properties();

    public GenericMethods(WebDriver webDriver) {
        this.driver = webDriver;
        fluentWait = new FluentWait<>(driver).withTimeout(fluentWaitDuration).pollingEvery(pollingInterval).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class).ignoring(ElementClickInterceptedException.class);
        javascriptExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
        new AssertionLibrary(webDriver);
    }

    public void setRunTimeProperty(String key, String value) {
        runTimeProperties.put(key, value);
    }

    public void setRunTimeProperty(String key, List<String> value) {
        runTimeProperties.put(key, value);
    }

    public String getRunTimeProperty(String key) {
        String value = null;
        try {
            value = replaceArgumentWithRunTimeProperties(runTimeProperties.getProperty(key).toString());
        } catch (Exception e) {
            Log.debug("Fail to get runtimeproperty for key:--> " + key + " >>> Exception accured :--> " + e.getMessage());
            return null;
        }
        return value;
    }

    public String replaceArgumentWithRunTimeProperties(String input) {
        if (input.contains("{$")) {
            int firstIndex = input.indexOf("{$");
            int lastIndex = input.indexOf("}", firstIndex);
            String key = input.substring(firstIndex + 2, lastIndex);
            String value = getRunTimeProperty(key);
            input = input.replace("{$" + key + "}", value);
            return replaceArgumentWithRunTimeProperties(input);

        } else {
            return input;
        }
    }

    protected static void consoleLog(Object msg) {
        Log.info(String.valueOf(msg));
    }

    protected static void consoleLogWarn(Object msg) {
        Log.warn(String.valueOf(msg));
    }

    protected void log(Object msg, boolean isScreenshotRequire) {
        Log.info(String.valueOf(msg));
        if (!isScreenshotRequire) ExtentTestManager.getTest().pass(String.valueOf(msg));
        else Screenshots.addStepWithScreenshotInReport(driver, String.valueOf(msg));

    }

    protected void logWarning(Object msg, boolean isScreenshotRequire) {
        Log.debug(String.valueOf(msg));
        if (!isScreenshotRequire) ExtentTestManager.getTest().warning(String.valueOf(msg));
        else Screenshots.addWarningStepWithScreenshotInReport(driver, String.valueOf(msg));
    }

    protected void logInfo(Object msg, boolean isScreenshotRequire) {
        Log.info(String.valueOf(msg));
        if (!isScreenshotRequire) ExtentTestManager.getTest().info(String.valueOf(msg));
        else Screenshots.addStepWithScreenshotInReport(driver, String.valueOf(msg));
    }

    /**
     * will log line in to report
     *
     * @param msg
     */

    public void log(Object msg) {
        Log.info(String.valueOf(msg));
        ExtentTestManager.getTest().pass(String.valueOf(msg));
    }

    public void logFail(Object msg) {
        Log.info(String.valueOf(msg));
        ExtentTestManager.getTest().fail(String.valueOf(msg));
    }

    ///used for UI with a screenshot by default
//    public void logFail(Object msg, boolean isHardAssert) {
//        Log.error(String.valueOf(msg));
//        ExtentTestManager.getTest().fail(String.valueOf(msg));
//        if (isHardAssert) AssertionLibrary.assertTrue(false, String.valueOf(msg), true);
//    }

    public void logFail(Object msg, boolean isScreenshotRequire, boolean isHardAssert) {
        Log.error(String.valueOf(msg));
        if (!isScreenshotRequire) ExtentTestManager.getTest().fail(String.valueOf(msg));
        else Screenshots.addFailStepWithScreenshotInReport(driver, String.valueOf(msg));
        if (isHardAssert) Assert.fail("STAUS : FAIL");
    }

    /**
     * this method will highligth element
     *
     */
    private void highlightElement(WebElement element) {
        javascriptExecutor.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "color: yellow; border: 2px solid cyan;");
        WaitUtils.sleep(250);
        javascriptExecutor.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
    }

    /**
     * this method will return random string
     *
     * @param num
     * @return string
     */
    public static String getRandomString(int num) {
        return RandomStringUtils.randomAlphabetic(num);
    }

    /**
     * this method will open new tab
     */

    public void openNewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open()");
    }

    /**
     * this method will return user dir
     *
     * @return string
     */
    public String getUserDir() {
        return System.getProperty("user.dir");
    }

    /**
     * this method checks if the element is present on page. Will look for it int
     * times with 500ms intervals
     *
     * @param by,iteration
     * @return boolean
     */
    protected boolean isElementVisible(final By by, int iteration) {

        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        int i = 0;
        boolean flag = false;
        do {
            try {
                driver.findElement(by);
                setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
                flag = true;
                i = iteration + 1;
            } catch (NoSuchElementException e) {
                ++i;
                WaitUtils.sleep(750);
                Log.info("Waiting for visibility off:-->" + by);
            }
        } while (i < iteration);

        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;

    }

    public void waitTillReadyStateComplete(int time) {
        new WebDriverWait(driver, Duration.ofSeconds(time)).until(d -> javascriptExecutor.executeScript("return document.readyState").equals("complete"));
    }

    /**
     * returns true if Jquery been used.     *
     *
     * @return List<WebElement>
     */
    public boolean IsJqueryBeingUsed() {
        Object isTheSiteUsingJQuery = javascriptExecutor.executeScript("return window.jQuery != undefined");
        return (boolean) isTheSiteUsingJQuery;
    }

    /**
     * returns true if Jquery Has Loaded .     *
     *
     * @return List<WebElement>
     */

    public boolean JqueryHasLoaded() {
        int timeout = 5;
        Object hasTheJQueryLoaded = javascriptExecutor.executeScript("jQuery.active === 0");
        while (hasTheJQueryLoaded == null || (!(boolean) hasTheJQueryLoaded && timeout > 0)) {
            WaitUtils.sleep(250);
            timeout--;
            hasTheJQueryLoaded = javascriptExecutor.executeScript("jQuery.active === 0");
            if (timeout != 0) continue;
            System.out.println("JQuery is being used by the site but has failed to successfully load.");
            return false;
        }
        return (boolean) hasTheJQueryLoaded;
    }

    public void waitForAngularFinishedProcessing(int time) {
        new WebDriverWait(driver, Duration.ofSeconds(time))
                .until(driver -> ((JavascriptExecutor) driver).executeScript("return (window.angular !== undefined) && (angular.element(document).injector() !== undefined) && (angular.element(document).injector().get('$http').pendingRequests.length === 0)"));
        System.out.println("DONE");
    }

    public ExpectedCondition<Boolean> angularHasFinishedProcessing() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("return (window.angular !== undefined) && (angular.element(document).injector() !== undefined) && (angular.element(document).injector().get('$http').pendingRequests.length === 0)").toString());
            }
        };
    }

    /**
     * returns true if Angular Is Being Used.     *
     *
     * @return List<WebElement>
     */

    public boolean AngularIsBeingUsed() {
        String UsingAngular = "if (window.angular){return true;}";
        Object isTheSiteUsingAngular = javascriptExecutor.executeScript(UsingAngular);
        return (boolean) isTheSiteUsingAngular;
    }

    /**
     * returns true if Angular Has Loaded .
     *
     * @return List<WebElement>
     */

    public boolean AngularHasLoaded() {
        int timeout = 5;
        String HasAngularLoaded = "return (window.angular !== undefined) && (angular.element(document.body).injector() !== undefined) && (angular.element(document.body).injector().get('$http').pendingRequests.length === 0)";

        Object hasTheAngularLoaded = javascriptExecutor.executeScript(HasAngularLoaded);
        while (hasTheAngularLoaded == null || (!(boolean) hasTheAngularLoaded && timeout > 0)) {
            WaitUtils.sleep(250);
            timeout--;

            hasTheAngularLoaded = javascriptExecutor.executeScript(HasAngularLoaded);
            if (timeout != 0) continue;
            System.out.println("Angular is being used by the site but has failed to successfully load.");
            return false;

        }
        return (boolean) hasTheAngularLoaded;
    }

    public void untilAngularFinishHttpCalls() {
        final String javaScriptToLoadAngular = "var injector = window.angular.element('body').injector();" + "var $http = injector.get('$http');" + "return ($http.pendingRequests.length === 0)";

        ExpectedCondition<Boolean> pendingHttpCallsCondition = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript(javaScriptToLoadAngular).equals(true);
            }
        };
        new WebDriverWait(driver, fluentWaitDuration).until(pendingHttpCallsCondition);
    }

    public void get(String url) {
        driver.get(url);
        Screenshots.addStepWithScreenshotInReport(driver, "Application launched: <a href=\"" + url + "\">" + url + "</a>");
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);

    }

    protected void setImplicitWait(int duration) {
        driver.manage().timeouts().implicitlyWait(duration, TimeUnit.SECONDS);
    }

    /**
     * returns list of webElements.
     *
     * 
     * @return List<WebElement>
     */
    protected List<WebElement> getElements(final String locator) {
        return fluentWait.until(new ExpectedCondition<List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                return driver.findElements(By.xpath(locator));
            }
        });
    }

    /**
     * returns list of webElements.
     *
     * 
     * @return List<WebElement>
     */
    protected List<WebElement> getElements(final By by) {
        return fluentWait.until(new ExpectedCondition<List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                return driver.findElements(by);
            }
        });
    }

    /**
     * returns list of webElements.
     *
     * 
     * @return List<WebElement>
     */
    protected List<WebElement> getElements(final By by, int waitTime) {
        List<WebElement> elements = new ArrayList<WebElement>();
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        for (int i = 0; i < waitTime; i++) {
            try {
                elements = driver.findElements(by);
                if (elements.size() > 0) {
                    break;
                }
            } catch (Exception e) {
                Log.error("Fail to find elements:-->" + by + "\nEXCEPTION OCCURED:\n" + e.getMessage() + "\nEXCEPTION CAUSE:\n" + e.getCause());
                WaitUtils.sleep(1000);
            }
        }
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return elements;
    }

    /**
     * returns the first instance of webElement
     *
     * 
     * @return WebElement
     */
    protected WebElement getElement(final String locator) {
        return fluentWait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.xpath(locator));
            }
        });
    }

    /**
     * returns the first instance of webElement
     *
     * 
     * @return WebElement
     */
    protected WebElement getElement(final By by) {
        return fluentWait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });
    }

    /**
     * returns the list of visible webElements
     *
     * 
     * @return WebElement
     */

    public List<WebElement> getVisibleElements(final By by) {
        return fluentWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    public List<WebElement> getVisibleElements(final String locator) {
        return fluentWait.until(ExpectedConditions.visibilityOfAllElements(getElements(locator)));
    }

    /**
     * this method checks if the element is present on page.
     *
     * 
     * @return boolean
     */
    protected boolean isElementVisibleOnPage(final By by) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        boolean flag = false;
        WebElement element = getElement(by);

        try {
            new WebDriverWait(driver, fluentWaitDuration).until(ExpectedConditions.visibilityOf(element));
            flag = true;
        } catch (Exception ex) {

        }
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;
    }

    /**
     * this method checks if the element is present on page.
     *
     * 
     * @return boolean
     */
    protected boolean isElementOnPage1(final String locator) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        boolean flag = !getElements(locator).isEmpty();
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;
    }

    protected boolean isElementOnPage(final String locator) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        boolean flag = false;
        List<WebElement> elementList = getElements(locator);
        if (!elementList.isEmpty()) {
            try {
                new WebDriverWait(driver, fluentWaitDuration).until(ExpectedConditions.visibilityOf(elementList.get(0)));
                flag = true;
            } catch (Exception ex) {

            }

        } else {
            return flag;
        }
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;
    }

    /**
     * this method checks if the element is present on page.
     *
     * 
     * @return boolean
     */
    protected boolean isElementOnPage(final By by) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        boolean flag = !getElements(by).isEmpty();
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;
    }

    /**
     * this method checks if the element is present on page.
     *
     * 
     * @return boolean
     */
    protected boolean isElementDisplayedOnPage(final By by) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        boolean flag = false;
        List<WebElement> elementList = getElements(by);
        if (!elementList.isEmpty()) {
            try {
                new WebDriverWait(driver, fluentWaitDuration).until(ExpectedConditions.visibilityOf(elementList.get(0)));
                flag = true;
            } catch (Exception ex) {

            }

        } else {
            return flag;
        }
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;
    }

    /**
     * this method checks if the element is present on page. Will look for it int
     * times with 750ms intervals
     *
     * ,int
     * @return boolean
     */
    protected boolean isVisible(final By by, int iteration) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        int i = 0;
        boolean flag = false;
        do {
            try {
                driver.findElement(by);
                setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
                flag = true;
                i = iteration + 1;
            } catch (Exception e) {
                ++i;
                WaitUtils.sleep(1000);
                Log.info("Waiting for visibility off:-->" + by);
            }
        } while (i < iteration);

        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;

    }

    /**
     * this method checks if the element is present on page. Will look for it int
     * times with 750ms intervals
     *
     * ,int
     * @return boolean
     */
    protected boolean isVisible(final String element, int iteration) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        int i = 0;
        boolean flag = false;
        do {
            try {
                driver.findElement(By.xpath(element));
                setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
                flag = true;
                i = iteration + 1;
            } catch (Exception e) {
                ++i;
                WaitUtils.sleep(1000);
                Log.info("Waiting for visibility off:-->" + element);
            }
        } while (i < iteration);

        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
        return flag;

    }

    /**
     * An expectation for checking that an element is either invisible or not
     * present on the DOM.
     *
     *  used to find the element
     */
    public void invisibilityOfElementLocated(final By by, int time) {
        int x = 0;
        WaitUtils.sleep(1000);
        do {
            if (!isElementPresentInDom(by)) {
                break;
            } else {
                x = x + 1;
                WaitUtils.sleep(1000);
            }

        } while (x < time);

    }

    /**
     * this method checks if the element is present on page.
     *
     * 
     * @return boolean
     */
    protected boolean isElementPresentInDom(final By by) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        try {
            driver.findElement(by);
            setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
            return true;
        } catch (NoSuchElementException e) {
            setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
            return false;
        }

    }

    /**
     * returns true, if element is enabled.
     *
     * 
     * @return boolean
     */
    protected boolean isEnabled(final String locator) {
        List<WebElement> elementList = getElements(locator);
        if (!elementList.isEmpty()) {
            return elementList.get(0).isEnabled();
        } else {
            return false;
        }
    }

    /**
     * returns true, if element is enabled.
     *
     * 
     * @return boolean
     */
    protected boolean isEnabled(final By by) {
        List<WebElement> elementList = getElements(by);
        if (!elementList.isEmpty()) {
            return elementList.get(0).isEnabled();
        } else {
            return false;
        }
    }

    /**
     * returns true, if element is clickable.
     *
     * 
     * @return boolean
     */

    protected Boolean isClickable(final By by) {
        List<WebElement> elementList = getElements(by);
        WebDriverWait wait = new WebDriverWait(driver, fluentWaitDuration);
        if (!elementList.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(elementList.get(0)));
            return true;
        } else {
            return false;
        }

    }

    protected Boolean isClickable(final By by, int time) {
        List<WebElement> elementList = getElements(by);
        WebDriverWait wait = new WebDriverWait(driver, fluentWaitDuration);
        if (!elementList.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(elementList.get(0)));
            return true;
        } else {
            return false;
        }

    }

    /**
     * returns WebElement, if element is clickable.
     *
     * 
     * @return boolean
     */

    protected WebElement getClickableElement(final By by) {
        WebElement element = getElement(by);
        int x = 0;
        do {
            if (element.isEnabled()) x = 10;
            else WaitUtils.sleep(1000);
            x = x + 1;

        } while (x < 10);
        fluentWait.until(ExpectedConditions.elementToBeClickable(element));
        return element;
    }

    /**
     * F returns WebElement, if element is clickable.
     *
     * 
     * @return boolean
     */

    protected WebElement getVisibleElement(final By by) {
        WebElement element = getElement(by);
        new WebDriverWait(driver, fluentWaitDuration).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(element));
        return element;
    }

    /**
     * returns true, if element is displayed.
     *
     * 
     * @return boolean
     */
    protected boolean isDisplayed(final String locator) {
        List<WebElement> elementList = getElements(locator);
        if (!elementList.isEmpty()) {
            return elementList.get(0).isDisplayed();
        } else {
            return false;
        }
    }

    /**
     * returns true, if element is displayed.
     *
     * 
     * @return boolean
     */
    protected boolean isDisplayed(final By by) {
        List<WebElement> elementList = getElements(by);
        if (!elementList.isEmpty()) {
            boolean flag = elementList.get(0).isDisplayed();
            return flag;
        } else {
            return false;
        }
    }

    /**
     * returns true, if element is selected.
     *
     * 
     * @return boolean
     */
    protected boolean isSelected(final String locator) {
        List<WebElement> elementList = getElements(locator);
        if (!elementList.isEmpty()) {
            return elementList.get(0).isSelected();
        } else {
            return false;
        }
    }

    /**
     * returns true, if element is selected.
     *
     * 
     * @return boolean
     */
    protected boolean isSelected(final By by) {
        List<WebElement> elementList = getElements(by);
        if (!elementList.isEmpty()) {
            return elementList.get(0).isSelected();
        } else {
            return false;
        }
    }

    /**
     * returns the number of instances of the element.
     *
     * 
     * @return size
     */
    protected int getElementSize(final String locator) {
        if (isElementOnPage(locator)) {
            return getElements(locator).size();
        } else {
            return 0;
        }
    }

    /**
     * returns the number of instances of the element.
     *
     * 
     * @return size
     */
    protected int getElementSize(final By by) {
        if (isElementOnPage(by)) {
            return getElements(by).size();
        } else {
            return 0;
        }
    }

    /**
     * This method sets input value using sendkeys function of selenium. Also
     * provides the feature of clean before setting the value.
     *
     * 
     * 
     */
    protected void setInputValue(final String locator, final String value, final boolean clearInput) {
        WebElement element = getElement(locator);
        if (clearInput) {
            element.clear();
        }
        element.sendKeys(value);
        Screenshots.addStepWithScreenshotInReport(driver, "Set value: " + value);
    }

    /**
     * This method sets input value using sendkeys function of selenium. Also
     * provides the feature of clean before setting the value.
     *
     * 
     * 
     */
    protected void setInputValue(final By by, final String value, final boolean clearInput) {
        try {
            if (isVisible(by, 10)) {
                if (clearInput) {
                    getElement(by).clear();
                }
                getElement(by).sendKeys(value);
                log("User set value:--> " + value, false);
            }

        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "User not able to enter:--> " + value + ", input field is not visible as result Exception accure as: " + e);
        }
    }

    /**
     * This method first clears and then sets input value using sendkeys function of
     * selenium.
     *
     * 
     * @param value
     */
    protected void setInputValue(final String locator, final String value) {
        WebElement element = getElement(locator);
        element.clear();
        element.sendKeys(value);
        Screenshots.addStepWithScreenshotInReport(driver, "Set value: " + value);
    }

    /**
     * This method first clears and then sets input value using sendkeys function of
     * selenium.
     *
     * 
     * 
     */
    protected void setInputValue(final By by, final String value) {
        boolean flag = false;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    highlightElement(getElement(by));
                    driver.findElement(by).clear();
                    driver.findElement(by).sendKeys(value);
                    log("User set value :--> <b><i><font color=blue>" + value + "</font></i></b>", false);
                    flag = true;
                    break;
                } catch (Exception e) {
                    Log.warn("Fail to set value:-->" + by + "\nEXCEPTION OCCURED:\n" + e.getMessage() + "\nEXCEPTION CAUSE:\n" + e.getCause());
                    WaitUtils.sleep(1000);
                }
            }

        }
        if (!flag)
            logFail("<b><i><font color=red>FAIL TO SET VALUE IN:--></font></i></b>" + by, true,true);

    }

    protected void setInputValue(final By by, final String value, final String elDescription) {
        boolean flag = false;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    driver.findElement(by).clear();
                    driver.findElement(by).sendKeys(value);
                    log("User set : <b><i><font color=blue>" + value + "</font></i></b> as " + elDescription.toUpperCase(), false);
                    flag = true;
                    break;
                } catch (Exception e) {
                    Log.error("Fail to set value:-->" + by + "\nEXCEPTION OCCURED:\n" + e.getMessage() + "\nEXCEPTION CAUSE:\n" + e.getCause());
                    WaitUtils.sleep(1000);
                }
            }

        }
        if (!flag)
            AssertionLibrary.assertTrue(false, "<b><i><font color=red>FAIL TO SET VALUE IN:--></font></i></b>" + by, true);

    }

    protected void bodyClick(){
        driver.findElement(By.xpath("//body")).click();
    };

    protected void setInputValueWC(final By by, final String value, final String elDescription) {
        boolean flag = false;
        String exception = null;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    highlightElement(getElement(by));
                    driver.findElement(by).sendKeys(value);
                    log("User set : <b><i><font color=blue>" + value + "</font></i></b> as " + elDescription.toUpperCase(), false);
                    flag = true;
                    break;
                } catch (Exception e) {
                    exception = e.getMessage();
                    WaitUtils.sleep(500);
                }
            }

        }
        if (!flag)
            logFail("<b><i><font color=red>FAIL TO SET VALUE IN :--> </font></i></b>" + by + "EXCEPTION OCCURRED :" + exception, true,true);
    }

    protected void setInputValuePW(final By by, final String value) {

        try {
            if (isVisible(by, 10)) {
                highlightElement(getElement(by));
                getElement(by).clear();
                getElement(by).sendKeys(value);
                log("User set password:--><b><i><font color=blue>xxxxxxxx</font></i></b>", false);
            }
        } catch (Exception e) {
            logFail("User not able to enter:--> " + value + ", input field is not visible as result Exception accure as: " + e,true,true);
        }
    }

    protected void setInputValue(final By by, Keys value) {
        WebElement element = getClickableElement(by);
        element.clear();
        element.sendKeys(value);
    }

    /**
     * This method sets input value using javascript Executor. Also provides the
     * feature of clean before setting the value.
     *
     * 
     * @param value
     * 
     */
    protected void setInputValueJS(final String locator, final String value, final boolean clearInput) {
        WebElement element = getElement(locator);
        if (clearInput) {
            element.clear();
        }
        javascriptExecutor.executeScript("arguments[0].value='" + value + "';", element);

    }

    /**
     * This method sets input value using javascript Executor. Also provides the
     * feature of clean before setting the value.
     *
     * 
     * @param value
     * 
     */
    protected void setInputValueJS(final By by, final String value, final boolean clearInput) {
        WebElement element = getElement(by);
        if (clearInput) {
            element.clear();
        }
        javascriptExecutor.executeScript("arguments[0].value='" + value + "';", element);

    }

    /**
     * This method sets input value using javascript Executor. *
     *
     * 
     * @param value
     */
    protected void setInputValueJSwc(final By by, final String value) {
        WebElement element = getElement(by);
        javascriptExecutor.executeScript("arguments[0].value='" + value + "';", element);
        try {
            javascriptExecutor.executeScript("arguments[0].setAttribute('" + value + "', arguments[1])", element);
        } catch (Exception e) {

        }
        try {
            javascriptExecutor.executeScript("document.getElementBy('" + by + "').setAttribute('value', '" + value + "')");
        } catch (Exception e) {

        }
        log("User set value:--> <b><i><font color=blue>" + value + "</font></i></b>", false);
    }

    /**
     * This method sets input value using Robot.
     *
     * @param value
     */
    protected void setInputValueRobot(final String value) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, stringSelection);
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            WaitUtils.sleep(250);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            WaitUtils.sleep(500);
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        log("User set value:--> <b><i><font color=blue>" + value + "</font></i></b>", false);
    }

    /**
     * This method sets input value using javascript Executor.
     *
     * 
     * @param value
     * 
     */
    protected void setInputValueJS(final String locator, final String value) {
        WebElement element = getElement(locator);
        element.clear();
        javascriptExecutor.executeScript("arguments[0].value='" + value + "';", element);
        log("Set value: " + value, false);
    }

    /**
     * This method sets input value using javascript Executor.
     *
     * 
     * @param value
     * 
     */
    protected void setInputValueJS(final By by, final String value) {
        try {
            // highlightElement(getElement(by));
            // wait.until(ExpectedConditions.visibilityOf(getElement(by)));
            // getElement(by).clear();
            javascriptExecutor.executeScript("arguments[0].value='" + value + "';", getElement(by));
            log("User set value:--> <b><i><font color=blue>" + value + "</font></i></b>", false);

        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "Error in entering value" + "</br> EXCEPTION OCCURED:</br>" + e.toString() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(), true);
        }
    }

    /**
     * This method sets input value using javascript Executor.
     *
     * 
     * @param value
     * 
     */
    protected void setInputValueJSinnerHTML(final By by, final String value) {
        try {
            javascriptExecutor.executeScript("arguments[0].innerHTML='" + value + "';", getElement(by));
            log("User set value:--> <b><i><font color=blue>" + value + "</font></i></b>", false);

        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "Error in entering value" + "</br> EXCEPTION OCCURED:</br>" + e.toString() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(), true);
        }
    }

    /**
     * This method clears the input field value.
     *
     * 
     */
    protected void clearElement(final String locator) {
        getElement(locator).clear();
    }

    /**
     * This method clears the input field value.
     *
     * 
     */
    protected void clearElement(final By by) {
        getElement(by).clear();
    }

    /**
     * This method returns the text.
     *
     * 
     * @return String
     */
    protected String getText(final String locator) {
        String value = null;
        if (isVisible(locator, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    // highlightElement(getElement(by));
                    value = getElement(locator).getText();
                    break;
                } catch (Exception e) {
                    Log.info("Retry to capture text");
                    WaitUtils.sleep(1000);
                }
            }

        }
        return value;
    }

    /**
     * This method returns the text.
     *
     * 
     * @return String
     */
    protected String getText(final By by) {
        String value = null;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    value = getElement(by).getText();
                    if (!empty(value)) {
                        value.trim();
                    }
                    break;
                } catch (Exception e) {
                    Log.info("Attempting to capture text from the element again");
                    WaitUtils.sleep(1000);
                }
            }
        }
        return value;
    }

    /**
     * This method returns text.
     *
     * 
     */
    protected String getTextJS(final By by) {
        String value = null;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    // highlightElement(getElement(by));
                    value = (String) javascriptExecutor.executeScript("return arguments[0].innerHTML;", getElement(by));
                    // value = (String) javascriptExecutor.executeScript("return
                    // arguments[0].innerText;", getElement(by));
                    // value = (String) javascriptExecutor.executeScript("return
                    // arguments[0].textContent;", getElement(by));
                    break;
                } catch (Exception e) {
                    WaitUtils.sleep(1000);
                }
            }

        }
        return value;
    }

    /**
     * This method returns the text.
     *
     * 
     * @return List<String>
     */
    protected List<String> getListOfText(final By by) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getElements(by).size(); i++) {
            if(getElements(by).get(i).getText()==null){
                list.add(getElements(by).get(i).getText());
            }else{
                list.add(getElements(by).get(i).getText().trim());
            }
        }
        return list;
    }

    /**
     * This method returns the value of mentioned attribute.
     *
     * 
     * @param attribute
     * @return String
     */
    protected String getAttribute(final String locator, final String attribute) {
        return getElement(locator).getAttribute(attribute);
    }

    /**
     * This method returns the value of mentioned attribute.
     *
     * 
     * @param attribute
     * @return String
     */
    protected String getAttribute(final By by, final String attribute) {
        return getElement(by).getAttribute(attribute);
    }

    /**
     * This method returns the css value of mentioned field.
     *
     * 
     * @param attribute
     * @return String
     */
    protected String getCssValue(final String locator, final String attribute) {
        return getElement(locator).getCssValue(attribute);
    }

    /**
     * This method returns the css value of mentioned field.
     *
     * 
     * @param attribute
     * @return String
     */
    protected String getCssValue(final By by, final String attribute) {
        return getElement(by).getCssValue(attribute);
    }

    /**
     * This method clicks using javascript executor.
     *
     * 
     */
    protected void clickElementJS(final String locator) {
        javascriptExecutor.executeScript("arguments[0].click();", getElement(locator));
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
    }

    /**
     * This method clicks using javascript executor.
     *
     * 
     */
    protected void clickElementJSSetAttributeOff(final By by) {
        javascriptExecutor.executeScript("arguments[0].setAttribute('unselectable', 'off')", getElement(by));
        javascriptExecutor.executeScript("arguments[0].click();", getElement(by));
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
    }

    /**
     * This method clicks using javascript executor.
     *
     * 
     */
    protected void clickElementJS(final By by) {
        javascriptExecutor.executeScript("arguments[0].click();", getElement(by));
    }

    /**
     * This method clicks using javascript executor.
     *
     * 
     */
    protected void handlePopUpDeleteBtn(final By by) {
        javascriptExecutor.executeScript("arguments[0].click();", getElement(by));
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText().trim();
            System.out.println("Click on Delete button:" + alertText);
            WaitUtils.sleep(2000);
            alert.accept();
            WaitUtils.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method clicks using javascript executor. Add description to display in
     * the report.
     *
     * 
     */
    protected void clickElementJS(final String locator, final String description) {
        javascriptExecutor.executeScript("arguments[0].click();", getElement(locator));
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
    }

    /**
     * This method clicks using javascript executor. Add description to display in
     * the report.
     *
     * 
     */
    protected void clickElementJS(final By by, final String description) {
        try {
            // highlightElement(getElement(by));
            javascriptExecutor.executeScript("arguments[0].click();", getElement(by));
            log("User tap on :--> <b><i><font color=blue>" + description + "</font></i></b>", false);
        } catch (Exception e) {
            logFail( "Error in clicking on " + description + "</br> EXCEPTION OCCURED : </br>" + e.getMessage() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(),true,true);
        }

    }

    /**
     * This method first makes the element visible and then perform click action
     * using javascript.
     *
     * 
     */
    protected void makeElementVisibleAndClick(final String locator) {
        WebElement element = getElement(locator);
        javascriptExecutor.executeScript("arguments[0].style.display='block';", element);
        javascriptExecutor.executeScript("arguments[0].click();", element);
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
    }

    /**
     * This method first makes the element visible and then perform click action
     * using javascript.
     *
     * 
     */
    protected void makeElementVisibleAndClick(final By by) {
        WebElement element = getElement(by);
        javascriptExecutor.executeScript("arguments[0].style.display='block';", element);
        javascriptExecutor.executeScript("arguments[0].click();", element);
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
    }

    /**
     * This method first makes the element visible and then perform click action
     * using javascript. Add description to display in the report.
     *
     * 
     */
    protected void makeElementVisibleAndClick(final String locator, final String description) {
        WebElement element = getElement(locator);
        javascriptExecutor.executeScript("arguments[0].style.display='block';", element);
        javascriptExecutor.executeScript("arguments[0].click();", element);
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
    }

    /**
     * This method first makes the element visible and then perform click action
     * using javascript. Add description to display in the report.
     *
     * @param by
     */
    protected void makeElementVisibleAndClick(final By by, final String description) {
        WebElement element = getElement(by);
        javascriptExecutor.executeScript("arguments[0].style.display='block';", element);
        javascriptExecutor.executeScript("arguments[0].click();", element);
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
    }

    /**
     * This method performs the normal click operation of Selenium.
     *
     * 
     */
    protected void clickElement(final String locator) {
        getElement(locator).click();
        Screenshots.addStepWithScreenshotInReport(driver, "Click Action: ");
    }

    /**
     * This method performs the normal click operation of Selenium without adding
     * step in report.
     *
     * @param by
     */
    protected void clickElement(final By by) {
        setImplicitWait(DEFAULT_IMPLICIT_WAIT);
        for (int i = 0; i < 2; i++) {
            try {
                driver.findElement(by).click();
                break;
            } catch (Exception e) {
                WaitUtils.sleep(1000);
            }
        }
        setImplicitWait(ConfigProvider.getAsInt("IMPLICIT_WAIT"));
    }

    /**
     * This method performs the normal click operation of Selenium without adding
     * step in report.
     *
     * 
     */
    protected void clickElementWL(final By by, String description) {
        boolean flag = false;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    driver.findElement(by).click();
                    flag = true;
                    break;
                } catch (Exception e) {
                    WaitUtils.sleep(1000);
                }
            }

        }
        if (!flag) AssertionLibrary.assertTrue(false, description + " is not clickable.");
    }

    /**
     * This method performs the normal click operation of Selenium. Add description
     * to display in the report.
     *
     * 
     */
    protected void clickElement(final String locator, String description) {
        getElement(locator).click();
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
    }

    /**
     * This method performs the normal click operation of Selenium. Add description
     * to display in the report.
     *
     * 
     */
    protected void clickElement(final By by, String description) {
        boolean flag = false;
        if (isVisible(by, 10)) {
            for (int i = 0; i < 10; i++) {
                try {
                    // highlightElement(getElement(by));
                    driver.findElement(by).click();
                    // getElement(by).click();
                    log("User tap on :--> <b><i><font color=blue>" + description + "</font></i></b>", false);
                    flag = true;
                    break;
                } catch (Exception e) {
                    WaitUtils.sleep(1000);
                }
            }

        }
        if (!flag) logFail( description + " is not clickable.",true,true);

    }

    /**
     * This method shifts the focus away from the current element.
     *
     * 
     */
    protected void shiftFocusAway(final String locator) {
        getElement(locator).sendKeys(Keys.TAB);
    }

    /**
     * This method shifts the focus away from the current element.
     *
     * 
     */
    protected void shiftFocusAway(final By by) {
        getElement(by).sendKeys(Keys.TAB);
    }

    protected void sendKeyDelete(final By by) {
        getElement(by).sendKeys(Keys.DELETE);
    }

    protected void selectAll(final By by) {
        Actions a = new Actions(driver);
        a.moveToElement(getElement(by)).click();
        // Press CTRL-ALT
        a.keyDown(Keys.CONTROL)
                .sendKeys("A")
                .build()
                .perform();

        // Release CTRL-ALT keys
        a.keyUp(Keys.CONTROL)
                .build()
                .perform();
    }

    /**
     * This method select and click on element from list of webElements.
     *
     * 
     *   value
     */

    protected void selectElementFromList(final By by, String value) {
        List<WebElement> list = new ArrayList<WebElement>(getElements(by));
        for (WebElement element : list) {
            if (element.getText().contains(value)) {
                element.click();
                Screenshots.addStepWithScreenshotInReport(driver, "Click: " + value);
                break;
            }
        }

    }

    protected String getPageSource() {
        return driver.getPageSource();
    }

    protected void elementSelect(String elementName, String selectBy, String selectValue, String xpath) {
        try {
            Select oSelect = new Select(driver.findElement(By.xpath(xpath)));
            switch (selectBy) {
                case "VisibleText":
                    oSelect.selectByVisibleText(selectValue);
                    break;
                case "Index":
                    oSelect.selectByIndex(1);
                    break;
                case "Value":
                    oSelect.selectByValue(selectValue);
                    break;
            }
            Screenshots.addStepWithScreenshotInReport(driver, "Select Element ");
        } catch (Exception e) {
            e.printStackTrace();
            // throw new InvocationTargetException(new
            // TestStepFailedException(),"Step *elementSelect* is failed on >>
            // "+elementName);
        }
    }

    protected void elementSelectFromDropdownWithVisibleText(final By element, String visibleText) {
        try {
            select = new Select(getElement(element));
            select.selectByVisibleText(visibleText);
            log("User Select :--> <b><i><font color=blue>" + visibleText + "</font></i></b>", false);
        } catch (Exception e) {
            logFail("User Fail to select :--> <b><i><font color=blue>" + visibleText + "</font></i></b>", true,true);
        }
    }

    protected void elementSelectFromDropdownWithIndex(final By element, int index) {
        select = new Select(getElement(element));
        select.selectByIndex(index);
        Screenshots.addStepWithScreenshotInReport(driver, "Select Element ");
    }

    protected void waitPageLoad(final By xpath) {
        boolean blnElementFlag;
        int i = 0;
        try {
            Thread.sleep(5000);
            blnElementFlag = getElement(xpath).isDisplayed();
            while (blnElementFlag == true) {
                Thread.sleep(5000);
                blnElementFlag = getElement(xpath).isDisplayed();
                i = i + 1;
                if (i > 11) {
                    break;
                }
            }
        } catch (Exception e) {
            blnElementFlag = true;
            Log.debug("Page Sync Issue");
        }

    }

    protected void checkboxElement(final By strXpath) {
        WebElement checkbox = getElement(strXpath);
        checkbox.click();

    }

    protected void defaultContent() {
        driver.switchTo().defaultContent();

    }

    protected void AcceptAlert() {
        new WebDriverWait(driver, fluentWaitDuration).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

    }

    protected void DissmisstAlert() {
        new WebDriverWait(driver, fluentWaitDuration).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.dismiss();

    }

    protected String GetTextAlert() {
        String alertText = "Unable to locate Alert";
        try {
            new WebDriverWait(driver, fluentWaitDuration).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText().trim();
            return alertText;

        } catch (Exception e) {
            e.printStackTrace();
            return alertText;
        }
    }

    /// New Functions added
    protected boolean closeWindow(final String windowsId) throws Exception {
        boolean ret = false;
        String before = null;
        String curr = null;

        final Set<String> handles = driver.getWindowHandles();
        final Iterator<String> it = handles.iterator();
        // final boolean state = this.getErrorCapture();
        while (it.hasNext()) {
            curr = it.next().toString();
            if (curr.trim().equalsIgnoreCase(windowsId)) {
                try {
                    // this.setErrorCapture(false);
                    driver.switchTo().window(windowsId);
                    driver.close();
                    if (before != null) driver.switchTo().window(before);// honor the lifo order
                    // js.executeScript("window.close()");//use javascript
                    ret = true;
                    break;
                } catch (final Exception e) {
                    // if (state)
                    // Log.error("Error occurred closing windows");
                    break;
                } finally {
                    // this.setErrorCapture(state);
                }
            } else before = curr;
        }

        // Log.warn("Cannot find specified window to close");
        return ret;
    }

    /**
     * To scroll the screen to the specific WebElement (Note: This just does the
     * browser scroll)
     *
     * 
     */
    protected void scrollintoview(final String locator) {
        javascriptExecutor.executeScript("arguments[0].scrollIntoView();", getElement(locator));
        Screenshots.addStepWithScreenshotInReport(driver, "Scroll Page: ");
    }

    protected void scrollintoview(final WebElement el) {
        javascriptExecutor.executeScript("arguments[0].scrollIntoView();", el);
    }

    /**
     * To switch from current window to another window(Note: Used best when the
     * number of open windows is 2)
     *
     * @return
     * @throws Exception
     */
    protected String switchWindow() throws Exception {
        WebDriverWait wait;
        final String current = driver.getWindowHandle();

        wait = new WebDriverWait(driver, fluentWaitDuration);
        try {
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(final WebDriver d) {
                    return d.getWindowHandles().size() != 1;
                }
            });
        } catch (final TimeoutException t) {
            return driver.getWindowHandle();
        }

        final Set<String> handles = driver.getWindowHandles();
        handles.remove(current);
        final String newTab = handles.iterator().next();
        driver.switchTo().window(newTab);
        return newTab;

    }

    /**
     * Specify a browser window to switch to.
     *
     * @param node values are 'root' or 'child' or a valid windows id for current
     *             session
     * @return the identifier for the switched window
     * @throws Exception in case of not able to switch window
     **/
    public String switchWindow(final String node) throws Exception {
        winID = driver.getWindowHandle();
        if (node == null || node.trim().isEmpty())
            throw new Exception("A windows node id must be passed or node='root' or node='child' to switchWindow(node)");
        // final String current = driver.getWindowHandle();
        if (node.trim().equalsIgnoreCase("root") || node.trim().equalsIgnoreCase("parent")) {
            driver.switchTo().window(this.winID);
            return this.winID;

        } else if (node.trim().equalsIgnoreCase("child")) {
            return switchWindow();
        }
        // switch to windows i
        if (!isWindowExist(node))
            throw new Exception("Windows id=" + node + " is not a valid window in current session");
        driver.switchTo().window(node);
        return node;
    }

    /**
     * To check if the window with the specified windows ID exists
     *
     * @param windowsId
     * @return
     */
    public boolean isWindowExist(String windowsId) {
        boolean found = false;
        final Set<String> handles = driver.getWindowHandles();
        for (final String win : handles) {
            if (win.trim().equalsIgnoreCase(windowsId)) {
                found = true;
                break;
            }
        }
        return found;
    }

    // DSE Functions
    protected void closeBrowser() {
        driver.close();
    }

    public boolean isDefaultOptionFromDropDown(final By by, final String expectedSelectedOption) {
        String selectTag = new Select(getElement(by)).getFirstSelectedOption().getAttribute("value");
        return selectTag.equalsIgnoreCase(expectedSelectedOption);
    }

    public void SelectByValueDropDwn(final By by, final String elementToSelect) {
        Select sel = new Select(getElement(by));
        sel.selectByValue(elementToSelect);
    }

    public void SelectByValueDropDwn1(final By by, final String elementToSelect) {
        Select sel = new Select(getElement(by));
        // sel.deselectAll();
        sel.selectByValue(elementToSelect);
    }

    public void SelectByValueDropDwnByDeSelecting(final By by, final String elementToSelect) {
        Select sel = new Select(getElement(by));
        sel.deselectAll();
        sel.selectByValue(elementToSelect);
    }

    public void SelectByIndexDropDwn(final By by, final int elementToSelect) {
        Select sel = new Select(getElement(by));
        sel.selectByIndex(elementToSelect);
    }

    public void SelectByVisibleTextDropDwn(final By by, final String elementToSelect) {
        Select sel = new Select(getElement(by));
        sel.selectByVisibleText(elementToSelect.trim());
    }

    public void handleMultiWindows() {
        String mainWindow = driver.getWindowHandle();
        Iterator<String> itr = driver.getWindowHandles().iterator();
        while (itr.hasNext()) {
            if (!mainWindow.equals(itr.next())) {
                driver.switchTo().window(itr.next());
            }
        }
    }

    public void switchToParentWindow() {
        driver.switchTo().defaultContent();

    }

    public void mouseOverAndClick(final By by, final By elemClick) {
        WebElement ele = getElement(by);
        Actions action = new Actions(driver);
        action.moveToElement(ele).perform();
        action.click();
    }

    public void drugAndDrop(final By drag, final By drop) {
        try {
            // WebElement from_elem = driver.findElement(drag);
            // WebElement to_elem = driver.findElement(drop);
            WebElement from_elem = getElement(drag);
            WebElement to_elem = getElement(drop);
            new Actions(driver).moveToElement(from_elem).pause(Duration.ofMillis(350)).clickAndHold(from_elem).pause(Duration.ofMillis(350)).moveByOffset(1, 0).moveToElement(to_elem).moveByOffset(1, 0).pause(Duration.ofMillis(350)).release().perform();
        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "Error in drug and drop </br> EXCEPTION OCCURED:</br>" + e.toString() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(), true);
        }

    }

    public void drugAndDropJS(final By drag, final By drop) {
        javascriptExecutor.executeScript("function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n" + "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n" + "data: {},\n" + "setData: function (key, value) {\n" + "this.data[key] = value;\n" + "},\n" + "getData: function (key) {\n" + "return this.data[key];\n" + "}\n" + "};\n" + "return event;\n" + "}\n" + "\n" + "function dispatchEvent(element, event,transferData) {\n" + "if (transferData !== undefined) {\n" + "event.dataTransfer = transferData;\n" + "}\n" + "if (element.dispatchEvent) {\n" + "element.dispatchEvent(event);\n" + "} else if (element.fireEvent) {\n" + "element.fireEvent(\"on\" + event.type, event);\n" + "}\n" + "}\n" + "\n" + "function simulateHTML5DragAndDrop(element, destination) {\n" + "var dragStartEvent =createEvent('dragstart');\n" + "dispatchEvent(element, dragStartEvent);\n" + "var dropEvent = createEvent('drop');\n" + "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);\n" + "var dragEndEvent = createEvent('dragend');\n" + "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);\n" + "}\n" + "\n" + "var source = arguments[0];\n" + "var destination = arguments[1];\n" + "simulateHTML5DragAndDrop(source,destination);", getElement(drag), getElement(drop));
    }

    @SuppressWarnings("deprecation")
    public void drugAndDropByCoordinat(final By drag, final By drop) {
        Point coordinates1 = getElement(drag).getLocation();
        Point coordinates2 = getElement(drop).getLocation();
        try {
            Robot robot = new Robot();
            robot.mouseMove(coordinates1.getX(), coordinates1.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseMove(coordinates2.getX(), coordinates2.getY());
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (Exception e) {

        }
    }

    public void dragAndDropRobot(final By drag, final By drop) {
        new Actions(driver).dragAndDrop(getElement(drag), getElement(drop)).release().build().perform();
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void dragAndDrop(String from_xpath, String to_xpath) {
        WebElement from_elem = driver.findElement(By.xpath(from_xpath));
        WebElement to_elem = driver.findElement(By.xpath(to_xpath));
        new Actions(driver).moveToElement(from_elem).pause(Duration.ofSeconds(1)).clickAndHold(from_elem).pause(Duration.ofSeconds(1)).moveByOffset(1, 0).moveToElement(to_elem).moveByOffset(1, 0).pause(Duration.ofSeconds(1)).release().perform();
    }

    public int randomNumber(final int minValue, final int maxValue) {
        Random rnd = new Random();
        return minValue + rnd.nextInt(maxValue);
    }

    public void switchToChildWindow() {
        try {
            String mainWindow = driver.getWindowHandle();
            Iterator<String> itr = driver.getWindowHandles().iterator();
            while (itr.hasNext()) {
                if (mainWindow.equals(itr.next())) {
                    driver.switchTo().window(itr.next());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public String getMainWindow() {
        String mainWindow = driver.getWindowHandle();
        return mainWindow;
    }

    public void switchToGivenWindow(String wind) {
        driver.switchTo().window(wind);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    protected void setInputValueJSWithOutClear(final By by, final String value) {
        WebElement element = getElement(by);
        javascriptExecutor.executeScript("arguments[0].value='" + value + "';", element);
        Screenshots.addStepWithScreenshotInReport(driver, "Set value: " + value);
    }

    public boolean isAlertPresent() {
        boolean foundAlert = false;
        WebDriverWait wait = new WebDriverWait(driver, fluentWaitDuration);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            foundAlert = true;
        } catch (TimeoutException eTO) {
            foundAlert = false;
        }
        return foundAlert;
    }

    public void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(driver, fluentWaitDuration);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (TimeoutException eTO) {
            System.out.println("alert not found");
        }
    }

    public boolean VerifyBlankText(By by) {
        if (driver.findElement(by).getAttribute("value").equals("")) {
            return true;
        } else {
            return false;
        }
    }

    protected void navigateBackward() {
        driver.navigate().back();
    }

    protected void clickElementWithAlert(final By by, String description) {
        getElement(by).click();
        AcceptAlert();
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
    }

    public void clickElementJSWithOutScreenShot(final By by) {
        javascriptExecutor.executeScript("arguments[0].click();", getElement(by));
    }

    protected String clickElementWidPopup(final By by, String description) {
        String alertText = "";
        try {
            getElement(by).click();
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText().trim();
            System.out.println("Alert data: " + alertText);
            WaitUtils.sleep(2000);
            alert.accept();

            WaitUtils.sleep(2000);

        } catch (Exception e) {

            System.out.println("catch block entry");
            e.printStackTrace();
        }
        return alertText;
    }

    public String getAlertText() {
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText().trim();
        WaitUtils.sleep(2000);
        alert.accept();

        return alertText;
    }

    public static String parseIntoString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    protected String clickElementWidPopupCancel(final By by, String description) {
        String alertText = "";
        try {
            getElement(by).click();
            Screenshots.addStepWithScreenshotInReport(driver, "Click: " + description);
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText().trim();
            System.out.println("Alert data: " + alertText);
            WaitUtils.sleep(2000);
            alert.dismiss();
            WaitUtils.sleep(2000);

        } catch (Exception e) {

            System.out.println("catch block entry");
            e.printStackTrace();
        }
        return alertText;
    }

    public File getLatestFilefromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    protected void scrollJsExecutor() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("scroll(0,250);");
    }

    public void navigateBack() {
        driver.navigate().back();
        // Screenshots.addStepWithScreenshotInReport(driver, "Application
        // launched: <a href=\"" + url + "\">" + url + "</a>");
    }

    protected void switchNewtab() {
        String parent = driver.getWindowHandle();
        Set<String> s1 = driver.getWindowHandles();
        Iterator<String> I1 = s1.iterator();
        while (I1.hasNext()) {
            String child_window = I1.next();
            if (!parent.equals(child_window)) {
                driver.switchTo().window(child_window);
                System.out.println(driver.switchTo().window(child_window).getTitle());

            }
        }
    }

    public void clickCordinates(int x, int y, final By by) {
        WebElement element = getElement(by);
        Point point = element.getLocation();
        x = point.getX();
        y = point.getY();
        System.out.println("xxxx" + x);
        System.out.println("yyyy" + y);
        // Actions builder = new Actions(driver);
        Actions action = new Actions(driver);

        action.moveToElement(element, x, y).click().build().perform();
    }

    // setPassword to encrypt the password

    protected void setDecryptPassword(final By by, final String encryptedString) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {

        String decryptedText = null;
        try {
            myEncryptionKey = "WelcomeToBYNMNextGenFramework";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebElement element = getElement(by);
        element.clear();
        element.sendKeys(decryptedText);
        Screenshots.addStepWithScreenshotInReport(driver, SET_INPUT + encryptedString);
    }

    protected void RigtClickElement(final By by) {
        Actions action = new Actions(driver);
        action.contextClick(getElement(by)).build().perform();
    }

    protected void DoubleClick(final By by, final String value) {
        Actions action = new Actions(driver);
        try {
            WebElement el = getElement(by);
            el.click();
            action.doubleClick(el).perform();
            log("Double Click by action: " + value, false);
        } catch (Exception e) {
            Log.error("Unable to perform a double click,\nEXCEPTION OCCURED:\n" + e.getMessage());
            AssertionLibrary.assertTrue(false, "Fail to double click on " + value + "</br> EXCEPTION OCCURED:</br>" + e.getMessage(), true);
        }
    }

    public void doubleClickJS(By by, String value) {
        try {
            javascriptExecutor.executeScript("arguments[0].dispatchEvent(new MouseEvent('dblclick', { bubbles: true }));", getElement(by));
            log("Double Click by action: " + value, false);
        } catch (Exception e) {
            Log.error("Unable to perform a double click,\nEXCEPTION OCCURED:\n" + e.getMessage());
            AssertionLibrary.assertTrue(false, "Fail to double click on " + value + "</br> EXCEPTION OCCURED:</br>" + e.getMessage(), true);
        }
    }

    protected void DoubleClick(final By by) {
        Actions action = new Actions(driver);
        action.doubleClick(getElement(by)).perform();
        WaitUtils.sleep(250);
    }

    public void switchtoFrame(By by) {
        try {
            driver.switchTo().frame(getElement(by));
        } catch (Exception e) {
            Log.error("Unable to switch frame,\nEXCEPTION OCCURED:\n" + e.getMessage() + "\nEXCEPTION CAUSE:\n" + e.getCause());
            AssertionLibrary.assertTrue(false, "Error in switching to frame" + "</br> EXCEPTION OCCURED:</br>" + e.getMessage() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(), true);
        }

    }

    public WebDriver getDriver() {
        return CucumberRuntime.get().getDriver();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    public void mouseOverAndVerify(final By by, String value) {
        try {
            if (isVisible(by, 10)) {
                WebElement ele = getElement(by);
                Actions action = new Actions(driver);
                action.moveToElement(ele).perform();
                log("User move mouse over:--> <b><i><font color=blue>" + value + "</font></i></b>", true);
            }
        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "User not able to move mouse over:--> " + value + ", as result Exception accure as: " + e.getMessage());
        }

    }

    public void moveByOffset(final By by, int x, int y) {
        try {
            if (isVisible(by, 10)) {
                WebElement ele = getElement(by);
                Actions action = new Actions(driver);
                action.moveByOffset(x, y).perform();
                log("User move by Offset", false);
            }
        } catch (Exception e) {
            AssertionLibrary.assertTrue(false, "User not able to move mouse by Offset:--> " + x + " : " + y + ", as result Exception accure as: " + e.getMessage());
        }

    }

    public void mouseOverAndVerify(final String by) {
        WebElement ele = getElement(by);
        Actions action = new Actions(driver);
        action.moveToElement(ele).perform();
    }

    protected void scrollintoview(final By locator) {
        try {
            javascriptExecutor.executeScript("arguments[0].scrollIntoView();", getElement(locator));
        } catch (Exception e) {
            Log.error("unable to scroll in to view,\nEXCEPTION OCCURED:\n" + e.toString() + "\nEXCEPTION CAUSE:\n" + e.getCause());
            AssertionLibrary.assertTrue(false, "Error in fetching attribute value from availability field" + "</br> EXCEPTION OCCURED:</br>" + e.toString() + "</br> EXCEPTION CAUSE:</br>" + e.getCause(), true);

        }
    }

    protected void scrollJsExecutorTillElementFound(By by) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", getElement(by));

    }

    protected void enter(final By by) {
        getElement(by).sendKeys(Keys.ENTER);
    }

    protected void enter() {
        try {
            Robot robot = new Robot();
            WaitUtils.sleep(1250);
            robot.keyPress(KeyEvent.VK_ENTER);
            WaitUtils.sleep(250);
            robot.keyRelease(KeyEvent.VK_ENTER);
            WaitUtils.sleep(1250);
        } catch (Exception ex) {

        }
    }

    protected void tab() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_TAB);
            WaitUtils.sleep(250);
            robot.keyRelease(KeyEvent.VK_TAB);
            WaitUtils.sleep(250);
        } catch (Exception ex) {

        }
    }

    protected void tab(final By by) {
        getElement(by).sendKeys(Keys.TAB);
    }

    protected void delete() {
        try {
            Robot robot = new Robot();
            WaitUtils.sleep(150);
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.keyRelease(KeyEvent.VK_DELETE);
            WaitUtils.sleep(250);
        } catch (Exception e) {

        }
    }

    protected void clickElementbyinteger(final int i, List<WebElement> wbplus, String string) {

        wbplus.get(i).click();
        Screenshots.addStepWithScreenshotInReport(driver, "Click: " + string);
    }

    /**
     * This method implements FluentWait for the spinner to disappear.
     *
     * 
     */

    public void waitForSpinnerToDisappear(final By _spinner) {
        try {
            fluentWait.withTimeout(Duration.ofSeconds(10)).pollingEvery(Duration.ofSeconds(1)).ignoring(Exception.class).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class).until(ExpectedConditions.invisibilityOf(getElement(_spinner)));
        } catch (Exception e) {
            Log.error("waitForSpinnerToDisappear throw the EXCEPTION : " + e.getMessage());
        }
    }

    /**
     * This method clicks on the Dropdown and selects the value by visible Text.
     * This method is used where the Dropdown uses Input Tag instead of Select tag.
     *
     */

    public void inputValueToDropdownField(By by, String value, String dropDownName) {
        WebElement element = getClickableElement(by);
        element.clear();
        element.sendKeys(value);
        getElement(by).sendKeys(Keys.ENTER);
        Screenshots.addStepWithScreenshotInReport(driver, "User selects " + value + " from " + dropDownName + " Dropdown field.");

    }

    /**
     * This method will clean all files from specific directory.
     *
     */
    public void cleanDirectory(String dirPath) {
        try {
            FileUtils.cleanDirectory(new File(dirPath));
        } catch (IOException e) {
            logWarning("FAIL to clean dir : " + e.getMessage(), false);
        }
    }

    protected void cleanFileInDirectory(String dirPath, String fileName) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        for (File listFile : files) {
            if (listFile.getName().contains(fileName)) {
                if(listFile.delete()){
                    consoleLog("Successfuly deleting file in : "+dirPath+fileName);
                }else{
                    consoleLog("Fail to Delete File in : "+dirPath+fileName);
                }
            }
        }
    }

    public boolean saveFile(String dirPath, String proccessName) {
        boolean flag = false;
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            flag = false;
        }
        for (File listFile : files) {
            if (listFile.getName().contains(proccessName)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * This method will loop till download complete.
     *
     */

    public boolean isDownloadComplete(String dirPath, int iterations) {
        boolean flag = false;
        for (int i = 0; i < iterations; i++) {
            if (isDirectoryNotEmpty(dirPath)) {
                for (File listFile : files) {
                    consoleLog("Download folder file name : "+listFile.getName());
                    if (listFile.getName().contains("crdownload")) {
                        i++;
                        consoleLog("Download is not complete yet");
                        WaitUtils.sleep(5000);
                    } else {
                        consoleLog("Download completed :" + DateUtils.getCurrentDate("HH:mm:ss"));
                        WaitUtils.sleep(500);
                        flag = true;
                        break;
                    }
                }

            } else {
                i++;
                WaitUtils.sleep(1000);
            }
            if (flag) break;
        }
        return flag;
    }

    /**
     * This method will check repository and return condition if empty.
     *
     *
     */
    public boolean isDirectoryNotEmpty(String dirPath) {
        WaitUtils.sleep(2000);
        File dir = new File(dirPath);
        files = dir.listFiles();
        if (files != null || files.length != 0) {
            for (File listFile : files) {
                consoleLog("DOWNLOAD FOLDER FILES : "+listFile.getName());
            }
            return true;
        } else {
            consoleLog("DOWNLOAD FOLDER IS EMPTY");
            return false;
        }
    }

    public boolean isFileDownloaded_Ext(String dirPath, String ext) {
        if (isDirectoryNotEmpty(dirPath)) {
            for (File listFile : files) {
                if (listFile.getName().contains(ext)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;

    }

    public String lastDownloadedFileName() {
        String DownloadFilepath = System.getProperty("user.home") + "\\Downloads\\";
        Path dir = Paths.get(DownloadFilepath);
        try {
            Optional<Path> lastFilePath = Files.list(dir) // here we get the
                    // stream with full
                    // directory listing
                    .filter(f -> !Files.isDirectory(f)) // exclude
                    // subdirectories from
                    // listing
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));// finally
            // get
            // the
            // last
            // file
            // using
            // simple
            // comparator
            // by
            // lastModified
            // field
            Path path = lastFilePath.get();
            Path FileName = path.getFileName();

            return FileName.toString();
        } catch (Exception e) {
            return null;
        }

    }

    public File getLastDownloadedFile() {
        File choice = null;

        try {

            File fl = new File("C:/Users/" + System.getProperty("user.name") + "/Downloads/");
            // Sleep to download file if not required can be removed
            // Thread.sleep(30000);
            File[] files = fl.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile();
                }
            });

            long lastMod = Long.MIN_VALUE;
            if (!(files[0] == null)) {
                for (File file : files) {
                    if (file.lastModified() > lastMod) {

                        choice = file;
                        lastMod = file.lastModified();

                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Exception while getting the last download file :" + e.getMessage());
        }

        // System.out.println("The last downloaded file is " +
        // choice.getPath());

        // System.out.println("The last downloaded file is " +
        // choice.getName());

        return choice;

    }

    public void movefiletoOtherlocation(String dirPath) throws Exception {

        WaitUtils.sleep(30000);
        File file = getLastDownloadedFile();

        // String Filename1 = file.getPath();

        String filename = file.getName();

        File oldfile = new File("C:/Users/" + System.getProperty("user.name") + "/Downloads/" + filename);

        String newName = "Trades.xlsx";
        String newFilePath = oldfile.getAbsolutePath().replace(oldfile.getName(), "") + newName;
        File newFile = new File(newFilePath);

        try {
            if (newFile.exists())

                newFile.delete();
            FileUtils.moveFile(oldfile, newFile);
            System.out.println("File moved");

        } catch (IOException e) {
            e.printStackTrace();
        }

        WaitUtils.sleep(10000);
        // File uploadfile = getLastDownloadedFile();

        String Uploadfilename = newFile.getName();

        String UploadPath = System.getProperty("user.dir") + "\\src\\test\\resources\\data\\Downloads\\" + Uploadfilename + " ";

        if (newFile.renameTo(new File(UploadPath))) {

            Log.info("File moved");

        } else {
            Log.info("File not moved");
        }

    }

    /**
     * returns true, if element is displayed.
     *
     * 
     * @return boolean
     */
    protected boolean isErrDisplayed(final By by) {
        fluentWaitDuration = Duration.ofSeconds(3);
        List<WebElement> elementList = getElements(by);
        fluentWaitDuration = Duration.ofSeconds(ConfigProvider.getAsInt("FLUENT_WAIT"));
        if (!elementList.isEmpty()) {
            return elementList.get(0).isDisplayed();
        } else {
            return false;
        }

    }

    /**
     * returns String
     *
     * @param encryptedPassword
     * @return String
     */
    protected static String decrypt(String encryptedPassword, SecretKeySpec key) {
        String iv = encryptedPassword.split(":")[0];
        String property = encryptedPassword.split(":")[1];
        String str = null;
        try {
            Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
            str = new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
        } catch (Exception e) {
            Log.error("Fail to decript string, following exceptions accured:-->" + e.getMessage());
        }
        return str;
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }

    public static SecretKeySpec getKey() {
        String password = "12345QWERTY";
        byte[] salt = new String("12345678").getBytes();
        int iterationCount = 40000;
        int keyLength = 128;
        try {
            keys = createSecretKey(password.toCharArray(), salt, iterationCount, keyLength);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return keys;
    }

    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
     * will print list off folders in specific dir
     */

    public void printAllFoldersFromSpecificDir() {

        String dirPath = System.getProperty("user.home") + "\\Documents\\Downloads\\";
        File file = new File(dirPath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(dirPath);
        System.out.println(Arrays.toString(directories));
    }

    /**
     * will check if string is not null or empty
     *
     * @param
     */
    public static boolean empty(final String s) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    /**
     * will set key value pair in to dat file
     *
     * @param
     */
    protected static void setProperty(String filePath, String key, String value) {
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            prop.load(fis);
            fis.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        // Setting the value to our properties file.
        prop.setProperty(key, value);

        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            prop.store(fos, filePath);
            fos.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * will get value from dat file
     *
     * @param
     */
    protected static String getProperty(String filePath, String key) {
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            prop.load(fis);
            fis.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return prop.getProperty(key).trim();
    }

    /**
     * Will return Clipboard value as String
     *
     */
    public static String getScreenshotAsString() {
        Clipboard systemClipboard = getSystemClipboard();
        DataFlavor dataFlavor = DataFlavor.stringFlavor;

        if (systemClipboard.isDataFlavorAvailable(dataFlavor)) {
            Object text;
            try {
                text = systemClipboard.getData(dataFlavor);
                return (String) text;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    private static Clipboard getSystemClipboard() {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit.getSystemClipboard();
    }

    public static void clearClipboard() {
        StringSelection stringSelection = new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public static boolean isNumeric1(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumeric2(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
        // '-' and decimal.
    }

    /**
     * Replaces the arguments like {$} present in input string with provided value
     *
     * @param input string in which some Argument is present
     * @return replaced string
     */
    public String replaceArgument(String input, String value) {
        if (input.contains("{$")) {
            input = input.replace("{$}", value);
            return replaceArgument(input, value);
        } else {
            return input;
        }
    }

    /**
     * Replaces the arguments like {$someArg} present in input string with its value
     * from RuntimeProperties
     *
     * @param input string in which some Argument is present
     * @return replaced string
     */
    public String replaceArgumentsWithRunTimeProperties(String input) {
        if (input.contains("{$")) {
            int firstIndex = input.indexOf("{$");
            int lastIndex = input.indexOf("}", firstIndex);
            String key = input.substring(firstIndex + 2, lastIndex);
            String value = getRunTimeProperty(key);
            input = input.replace("{$" + key + "}", value);
            return replaceArgumentsWithRunTimeProperties(input);
        } else {
            return input;
        }

    }

    protected String doubleToString(Object object) {

        if (object != null) {
            if (object instanceof BigDecimal) {
               // Log.info("object is BigDecimal:-->" + object);
                double d = ((BigDecimal) object).doubleValue();
                if (String.valueOf(d).equals("0.0") || String.valueOf(d).equals("-0.0")) {
                    return "null";
                } else {
                    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    df.setMaximumFractionDigits(340);
                    return String.valueOf(df.format(d));
                }
            } else if (object instanceof Double) {
               // Log.info("object is Double:-->" + object);
                if (object.toString().equals("0.0") || object.toString().equals("-0.0")) {
                    return "null";
                } else {
                    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    df.setMaximumFractionDigits(340);
                    return String.valueOf(df.format(object));
                }
            } else {
                Log.info("object is not instance off double:-->" + object);
                return String.valueOf(object);
            }
        } else {
           // Log.info("object is null :-->" + object);
            return "null";
        }

    }

    public static String concatQUERY(List<String> strings) {
        String string = "";
        for (String str : strings) {
            string = string + "'" + str + "',";
        }
        return removeComa(string);
    }

    public static String concatG1LoanNum(Set<String> strings) {
        String string = "";
        String missingSpace = "";
        for (String str : strings) {
            for (int space = 0; space < (12 - str.length()); space++) {
                missingSpace = missingSpace + " ";
            }
            string = string + "'" + str + missingSpace + "',";
            missingSpace = "";
        }
        return removeComa(string);
    }

    public static String removeComa(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
    ///////////////////////////////////Rome////////////////////////////////////

    public boolean softAssertPresentValidation(By locator, String elementName) {
        try {
            WebElement element = getElement(locator);
            if (element.isDisplayed()) {
                Log.info("Verified " + elementName + " is present on the page");
                return true;
            } else {
               logWarning(elementName + " is not present on the page",false);
                validationMismatch.add(elementName + " is not present on the page");
                return false;
            }
        } catch (Exception e) {
            logWarning(elementName + " is not present on the page",false);
            validationMismatch.add(elementName + " is not present on the page");
            return false;
        }
    }

    public static String getQuantity() {
        Random random = new Random(System.nanoTime());
        int randomInt = random.nextInt(1000000000) + 1;
        while (String.valueOf(randomInt).length() != 11) {
            randomInt = random.nextInt(1000000000) + 1;
        }
        String quant="7".concat(String.valueOf(randomInt));
        return quant;
    }

    protected void verifyContentFromFile(String relativePath) {
        verifyContentFromFile(relativePath, new HashMap<>());
    }
    protected void verifyContentFromFileSoft(String relativePath) {
        verifyContentFromFileSoft(relativePath, new HashMap<>());
    }
    protected void verifyContentFromFileSoft(String relativePath, Map<String, String> variables) {
        try{
            //initValidationStatus();
            InputStream inputStream = new FileInputStream(contentFilesLocation + relativePath);
            Yaml yaml = new Yaml();
            ContentValidation data = yaml.loadAs(inputStream, ContentValidation.class);
            Map<String, ContentValidationItem> contentToValidate = data.getContent();
            for (ContentValidationItem item : contentToValidate.values()) {
                String content = fillVariablesInString(item.getContent(), variables);
                verifyStringContent(item.getDescription(),
                        By.xpath(item.getXpath()),
                        content
                );
            }
        }catch(Exception e){
            logWarning("Failed opening content file with Exception "+e,false);
            org.junit.Assert.fail("Unable to read content file " + contentFilesLocation + relativePath);
        }
    }
    private String contentFilesLocation = System.getProperty("user.dir") + File.separator + "src" + File.separator
            + "main" + File.separator + "resources" + File.separator + "ApplicationFiles" + File.separator
            + "ContentValidationFiles" + File.separator;
    protected void verifyContentFromFile(String relativePath, Map<String, String> variables) {
        try{
            initValidationStatus();
            InputStream inputStream = new FileInputStream(contentFilesLocation + relativePath);
            Yaml yaml = new Yaml();
            ContentValidation data = yaml.loadAs(inputStream, ContentValidation.class);
            Map<String, ContentValidationItem> contentToValidate = data.getContent();
            for (ContentValidationItem item : contentToValidate.values()) {
                String content = fillVariablesInString(item.getContent(), variables);
                verifyStringContent(item.getDescription(),
                        By.xpath(item.getXpath()),
                        content
                );
            }
            validateStatus();
        }catch(Exception e){
            logWarning("Failed opening content file with Exception "+e,false);
            Assert.fail("Unable to read content file " + contentFilesLocation + relativePath);
        }
    }
    private String fillVariablesInString(String text, Map<String, String> variables) {
        String result = text;
        for (String s : variables.keySet()) {
            String key = "${" + s + "}";
            String value = variables.get(s);
            result = result.replace(key, value);
        }
        return result;
    }
    protected void verifyStringContent(String what, By locator, String content) {
        try{
            String expected = content;//ConfigProvider.getAsString(contentKey);
            String actual = getText(locator);
            if (isEmpty(actual))
                actual = getAttribute(locator, "value");
            if(!softAssert(what, actual, expected)) {
                String mismatchError = "Content mismatch: " + what + " :==>\n   Expected :--> " + expected + "\n   Actual :--> " + actual;
                validationMismatch.add(mismatchError);
            }
        }catch(TimeoutException e) {
            String unableToProcessError = "Content mismatch, unable to find element on document :--> " + what  + "\nCheck xpath :--> " + locator.toString();
            validationMismatch.add(unableToProcessError);
        }catch(Exception e) {
            String unableToProcessError = "Content mismatch, unexpected exception getting: " + what + " " + e.getMessage();
            validationMismatch.add(unableToProcessError);
        }
    }
    public static boolean isEmpty(String str) {
        return ((str == null) || str.isEmpty());
    }
    /**
     * soft assertion of 2 strings
     *
     * @param what
     * @param expected
     * @param actual
     */
    public boolean softAssert(String what, String actual, String expected) {
        if (expected == null & actual == null) {
            Log.info(what + " :==> | Expected :--> " + expected + " | Actual :--> " + actual);
            return true;
        }
        try {
            if (!actual.trim().equalsIgnoreCase(expected.trim())) {
                logWarning(what + " :==> | Expected :--> " + expected + " | Actual :--> " + actual,false);
                return false;
            } else {
               logInfo(what + " :==> | Expected :--> " + expected + " | Actual :--> " + actual,false);
                return true;
            }
        } catch (Exception e) {
            logWarning(what + " :==> | Expected :--> " + expected + " | Actual :--> " + actual,false);
            return false;
        }

    }

   ////////////////////////////COMPARISON////////////////////////
   public void compareMapToMapSoft(String what,Map<String, String> map1, Map<String, String> map2) {
       List<Map.Entry<String, String>> mismatches = compareMapsByValues(map1, map2);
       if(mismatches.isEmpty()){
           Log.info("COMPARE MAP's BY VALUE : PASS");
       }else{
           for(Map.Entry entry:mismatches){
               validationMismatch.add(what+ " MISMATCH : ["+entry.getKey()+"]["+entry.getValue()+"]");
           }
       }

   }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> compareMapsByValues(Map<K, V> map1, Map<K, V> map2) {
        List<Map.Entry<K, V>> mismatches = new ArrayList<>();

        for (Map.Entry<K, V> entry1 : map1.entrySet()) {
            K key1 = entry1.getKey();
            V value1 = entry1.getValue();

            if (map2.containsKey(key1)) {
                V value2 = map2.get(key1);
                if (value1.compareTo(value2) != 0) {
                    mismatches.add(entry1);
                }
            } else {
                mismatches.add(entry1);
            }
        }

        for (Map.Entry<K, V> entry2 : map2.entrySet()) {
            K key2 = entry2.getKey();
            V value2 = entry2.getValue();

            if (!map1.containsKey(key2)) {
                mismatches.add(entry2);
            }
        }

        return mismatches;
    }
    /////////////////////////////

    ArrayList<String> uiObjNotMatchingDB = null;  ArrayList<String> dbObjNotMatchingUi = null;

    protected void compareListToList(List<String> objA, List<String> objB) {
        uiObjNotMatchingDB = new ArrayList<>();
        dbObjNotMatchingUi = new ArrayList<>();

        for (String uiObject : objA) {
            boolean contained = false;
            for (String dbObj : objB) {
                if (uiObject.equals(dbObj)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                uiObjNotMatchingDB.add(uiObject);
            }
        }

        for (String dbObj : objB) {
            boolean contained = false;
            for (String uiObject : objA) {
                if (uiObject.equals(dbObj)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                dbObjNotMatchingUi.add(dbObj);
            }
        }

        if (uiObjNotMatchingDB.isEmpty() & dbObjNotMatchingUi.isEmpty()) {
            Log.info("objA content equal to objB content");
        } else {
            logWarning("************************ ObjA objects not returned by the ObjB *************************",false);
            uiObjNotMatchingDB.forEach((String name) -> {
                logWarning(name,false);
            });
            logWarning("************************ ObjB objects not returned by the ObjA *************************",false);
            dbObjNotMatchingUi.forEach((String name) -> {
                logWarning(name,false);
            });
            org.junit.Assert.fail("\n************************ ObjA obj not returned by the ObjB *************************\n "
                    + String.join(". \n ", uiObjNotMatchingDB)
                    + "\n************************ ObjB obj not returned by the ObjA *************************\n "
                    + String.join(". \n ", dbObjNotMatchingUi));
        }

    }

    /**
     * Soft assert two lists of string and will return a boolean
     *
     * @param actualList List of Strings from UI
     * @param expectedList  List of Strings as per requirements
     * @return The lists are matching or not
     */

    public boolean softAssertListValidation(List<String> actualList, List<String> expectedList) {
        boolean matching = false;
        List<String> actualNotMatchingExpected = new ArrayList<>();
        for (String actual : actualList) {
            boolean contained = false;
            for (String expected : expectedList) {
                if (actual.equals(expected)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                actualNotMatchingExpected.add(actual);
                validationMismatch.add("The actual element '" + actual + "' is not found in the expected list " + expectedList);
            }
        }

        if (actualNotMatchingExpected.isEmpty()) {
            matching = true;
            Log.info("The expected list " + expectedList + "\n\t\t\t\t- Is matching the actual list " + actualList);
        } else {
            validationMismatch.add("The expected list " + expectedList + "\nis not matching the actual list " + actualList);
            logWarning("Expected list is not matching actual list",false);
        }
        return matching;
    }

    protected List<String> validationMismatch;
    public void initValidationStatus() {
        validationMismatch = new ArrayList<String>();
    }
    public void validateStatus() {
        if (validationMismatch.size() > 0) {
            Assert.fail("Content validation failed:\n" + String.join("\n", validationMismatch));
        }else{
            //zoomOut();
            log("Content validation : PASS");
        }
    }




}
