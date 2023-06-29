package com.api.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cucumber.utilities.WaitUtils;
import com.rebar.utilities.Log;
import com.rebar.utilities.configprovider.ConfigProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class TokenGeneratorSFDM {

    public static String proxyURL;
    public static String getToken(final String userType) {
        String var10000 = ConfigProvider.getAsString("proxy.url");
        proxyURL = var10000 + ":" + ConfigProvider.getAsString("proxy.port");
        String token=null;
        String user = null;
        String secret = null;
        String appUrl = null;
        ChromeDriver driver =null;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--incognito");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        //options.addArguments("--ignore-ssl-errors=yes");
        //options.addArguments("--ignore-certificate-errors");
        options.addArguments("--window-size=1400,800");
        options.addArguments("--remote-allow-origins=*");
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
        options.setCapability( "goog:loggingPrefs", logPrefs );
        try{
            // WebDriverManager.chromedriver().proxy(proxyURL).setup();
            WebDriverManager.chromiumdriver().proxy(proxyURL).setup();
            Log.info("WebDriverManager setup complete");
            WaitUtils.sleep(2000);
            Log.info("INIT ChromeDriver");
            driver = new ChromeDriver(options);
            Log.info("ChromeDriver initilized");
        }catch(Exception e){
            Log.warn("Fail to launch chrome : exeption -->"+e.getMessage());
            Assert.fail();
        }
        switch (userType) {
            case "admin":
                user = System.getProperty("AdmUserName");
                secret = System.getProperty("AdmSecr");
                break;
            case "AMO":
                user = System.getProperty("AmoUserName");
                secret = System.getProperty("AmoSecr");
                break;
        }
        //setRunTimeProperty("user",user);
        switch (ConfigProvider.getAsString("SfdmEnv")) {
            case "dev":
                appUrl = ConfigProvider.getAsString("SfdmAppUrlDEV");
                break;
            case "qa":
                appUrl = ConfigProvider.getAsString("SfdmAppUrlQA");
                break;
            default:
                Assert.fail( "Fail to select execution env, please check your NextGen.properties in SfdmEnv : ");
        }
        //setRunTimeProperty("appUrl",appUrl);
        Log.info("URL :"+appUrl);
        Log.info("USER :"+user);
        driver.get(appUrl);
        WaitUtils.sleep(2000);
        Log.info("DRIVER LAUNCH URL : "+appUrl);

        By userName = By.xpath("//input[@id='username']");
        By secretInputField = By.xpath("//input[@id='password']");
        By sighnInBtn = By.xpath("//button[@name='signInBtn']");
        By sidePanel=By.xpath("//li[@aria-label='YIELDS']");
        By moduleYIELDS = By.xpath("//span[contains(text(),'YIELDS')]");

        getElement(userName, driver).sendKeys(user);
       // getElement(secretInputField,driver).sendKeys(decrypt(secret, getKey()));
        getElement(secretInputField,driver).sendKeys("Jan2023!@");
        getElement(sighnInBtn,driver).click();
        getElement(sidePanel,driver).click();

        List<LogEntry> entries = driver.manage().logs().get("performance").getAll();
        Log.info("DRIVER MANGE TO GET LIST OF LOGS");
        List<String> message=new ArrayList<>();
        for(LogEntry entry:entries){
            if(entry.getMessage().contains("https://url")&&entry.getMessage().contains("Bearer")){
                message.add(entry.getMessage());
            }
        }

        for(String jsonString:message){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonString);
                // Access headers as properties of the Java object
                JsonNode mNode = rootNode.get("message");
                JsonNode paramsNode = mNode.get("params");
                JsonNode requestNode = paramsNode.get("request");
                JsonNode headersNode = requestNode.get("headers");
                Iterator<String> fieldNames = headersNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    String headerValue = headersNode.get(fieldName).asText();
                    if (fieldName.contains("Authorization")) {
                        token = headerValue;
                        break;
                    }
                }
            }catch(Exception e){
                Log.warn("Fail to mapp JsonNode : exeption -->"+e.getMessage());
                Assert.fail();
            }

        }
        driver.close();
        //driver.quit();
        Log.info("CHROME QUIT");
        Log.info("TOKEN : "+token);
        return token;
    }
    private static Duration pollingInterval = Duration.ofMillis(ConfigProvider.getAsInt("POLLING_INTERVAL"));
    private static  Duration fluentWaitDuration = Duration.ofSeconds(ConfigProvider.getAsInt("FLUENT_WAIT"));
    protected static WebElement getElement(final By by, final ChromeDriver driver) {
        FluentWait<ChromeDriver> fluentWait = new FluentWait<>(driver).withTimeout(fluentWaitDuration).pollingEvery(pollingInterval).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class).ignoring(ElementClickInterceptedException.class).ignoring(ElementNotInteractableException.class);
        return fluentWait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });
    }

  }
