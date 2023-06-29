package com.cucumber.utilities;

//import io.appium.java_client.functions.ExpectedCondition;
import com.rebar.utilities.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class WaitUtils {

	public static void waitForPageToLoad(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		});

	}

	public static void sleep(long inMillis) {
		try {
			//Log.info(String.valueOf("Wait for : " + inMillis));
			Thread.sleep(inMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
