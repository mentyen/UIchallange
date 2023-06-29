package com.cucumber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.Log;
import com.rebar.utilities.SMSUtil;
import com.rebar.utilities.Screenshots;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;

/**
 * Runs each cucumber scenario found in the features as separated test
 * com.rebar.listeners.TestListener.class
 */
@Listeners(TestListener.class)
public abstract class AbstractTestNGCucumberTest {

	private io.cucumber.testng.TestNGCucumberRunner testNGCucumberRunner;
	public static HashMap<String, List<String>> tagsInScenario = new HashMap<>();
	
	//private final static String propertyFilePath = System.getProperty("user.dir");

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
	}

	public void folderCleanup() throws IOException {
		File file = new File(Screenshots.getScreenshotsFolderPath());
		if (file.exists())
			FileUtils.cleanDirectory(file);
	}
	
	@BeforeSuite(alwaysRun = true)
	public void loadSecretFromSMS() throws Exception {	
		if (System.getProperty("enableSMS", ConfigProvider.getAsString("enableSMS")).equalsIgnoreCase("yes")){
			Log.info("Download from SMS" );
			String propertyFilePath = SMSUtil.downloadSecretFromSMS();
			//String propertyFilePath ="C:\\SeleniumProjects\\path";
			SMSUtil.loadPropFromSMS(propertyFilePath);			
			Log.info("Property file path: "+propertyFilePath );
		}
		
	} 

	@Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void runScenario(PickleWrapper pickleWrapper,FeatureWrapper featureWrapper) throws Throwable {
		
		List<String> getTagNames = new ArrayList<>();
        for (String getTag : pickleWrapper.getPickle().getTags()) {
            getTagNames.add(getTag);
        }
        tagsInScenario.put(pickleWrapper.getPickle().getName(), getTagNames);        
		testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
	}

	/**
	 * Returns two dimensional array of PickleEventWrapper scenarios with their associated CucumberFeatureWrapper feature.
	 *
	 * @return a two dimensional array of scenarios features.
	 */
	@DataProvider
	public Object[][] scenarios() {
		if (testNGCucumberRunner == null) {
			return new Object[0][0];
		}
		return testNGCucumberRunner.provideScenarios();
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() {
		if (testNGCucumberRunner == null) {
			return;
		}
		testNGCucumberRunner.finish();
	}	
	
	@AfterSuite(alwaysRun = true)
	public void cleanupSMSFile()
	{
//		File dir = new File(System.getProperty("user.dir"));
//		if (!dir.exists())
//			return;
//		File[] files = dir.listFiles();
//		for (File file : files) {
//			if (!file.isDirectory()) {
//				if (file.getName().contains(".properties")) {
//					Log.info(file.getName() + " deleted");
//					file.delete();
//
//				}
//			}
//		}
		
	}
}
	
