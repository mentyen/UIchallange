package com.sample.test.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;

public class TestPage extends TestBase {

	// public WebDriver driver;

	// @BeforeMethod
	public void setUp() {

		System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver\\windows\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().fullscreen();
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.get("https://demoqa.com/keyboard-events-sample-form/");
	}

	private By element2 = By.cssSelector("textarea[id='permanentAddress']");

	public By getElement2() {
		return element2;
	}

	
	  @FindBy(id="permanentAddress") 
	 public WebElement element;
	 
	  public TestPage() { 
	 PageFactory.initElements(driver, this); 
	  }
	   
	

	@Test
	public void testing() {

		// System.out.println(element.isDisplayed());

		System.out.println(driver.findElement(getElement2()).isDisplayed());

		// System.out.println(driver.findElement(By.id("permanentAddress")).isDisplayed());

	}

	//@AfterMethod
	public void tearDown() {
		try {
			driver.quit();
		} catch (Exception ex) {

		}
	}

}
