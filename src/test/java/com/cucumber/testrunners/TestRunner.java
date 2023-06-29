package com.cucumber.testrunners;

import java.io.IOException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import com.cucumber.AbstractTestNGCucumberTest;
import io.cucumber.testng.CucumberOptions;
//"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
@CucumberOptions(features = "src/test/resources/", glue = {
		        "com.cucumber.stepdefinition" },
		        plugin = { "pretty", "summary","json:AutomationReports/Cucumber.json" },
				tags = " @uitest ", monochrome = true, dryRun = false
)

public class TestRunner extends AbstractTestNGCucumberTest {
	//set parrallel=true in order to initiate parralel execution
	@Override
	@DataProvider(parallel = true)
	public Object[][] scenarios() {
		return super.scenarios();
	}

	@AfterClass
	public void killChrome() throws IOException {

//		if (System.getProperty("os.name").contains("Windows1")) {
//			Runtime rt = Runtime.getRuntime();
//			try {
//				String username = System.getProperty("user.name");
//				String driverNametoClose_chrome = "chromedriver.exe";
//				rt.exec("taskkill /f /im chrome.exe /fi \"username eq ams\\" + username + "\"");
//
//				System.out.println("Successfully killed driver for user name: " + username);
//			} catch (IOException e) {
//				System.out.println(e);
//			}
//		}

	}
}