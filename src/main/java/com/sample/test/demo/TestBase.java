package com.sample.test.demo;

import static org.testng.Assert.fail;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestBase {

	private Configuration config;
	protected static WebDriver driver;
	protected String url;

	@BeforeClass(alwaysRun = true)
	public void init() throws Throwable {
		config = new Configuration();
		url = config.getUrl();
		initializelDriver();
		navigateToSite();
	}

	private void navigateToSite() {

		driver.get(url);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		try {
			driver.quit();

		} catch (Exception e) {
		}
	}

	private void initializelDriver() {
		if (config.getBrowser().equalsIgnoreCase("chrome")) {
			if (config.getPlatform().equalsIgnoreCase("mac")) {
				System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver/mac/chromedriver");
			} else {
				System.setProperty("webdriver.chrome.driver",
						"src/test/resources/chromedriver/windows/chromedriver.exe");
			}
			driver = new ChromeDriver();
		} else {
			fail("Unsupported bfrowser " + config.getBrowser());
		}

	}

	public static void sendKey(WebElement element, String key) {

		element.clear();
		element.sendKeys(key);
	}

	public static void radioButtonClick(WebElement element) {

		if (!element.isSelected()) {
			element.click();
		} else {
			System.out.println("Button is already selected");
		}
	}

	public static void selectValueFromDD(WebElement element, String text) {

		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		boolean isSelected = false;

		for (WebElement option : options) {
			String optionText = option.getAttribute("value").toString();
			if (optionText.equals(text)) {
				select.selectByValue(text);
				//System.out.println("Option with text " + text + " is selected");
				isSelected = true;
				break;
			}
		}
		if (!isSelected) {
			System.out.println("Option with text +" + text + "is not available");
		}
	}

	public static String getRandomName() {

		String generatedString = RandomStringUtils.randomAlphabetic(1);

		return ("John" + generatedString + "  White " + generatedString);
	}

	public static String getRandomEmail() {

		String generatedString = RandomStringUtils.randomAlphabetic(6);

		return (generatedString + "@gmail.com");
	}

	public static String getRandomPhoneNumber() {

		String generatedString = RandomStringUtils.randomNumeric(4);

		return ("440-570-" + generatedString);
	}

	public static String getOrderPrice(double actualPrice) {

		double price = actualPrice;
		int intPart = (int) actualPrice;
		double decimalPart = actualPrice - intPart;

		if (decimalPart == 0.0) {
			return Integer.toString(intPart);

		} else {
			return Double.toString(price);
		}

	}

	public static boolean isPaymentSelected(WebElement card, WebElement cash) {

		boolean iscreditCardiSSelected = card.isSelected();

		boolean iscashIsSelected = cash.isSelected();

		boolean isPaymentSelected = true;

		if (iscreditCardiSSelected == false && iscashIsSelected == false) {
			isPaymentSelected = false;
		}
		;
		return isPaymentSelected;
	}

}
