package com.cucumber;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.Log;
import com.rebar.utilities.extentreports.ExtentConfiguration;
import com.rebar.utilities.extentreports.ExtentTestManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.Status;

public class BDDReportListner implements ITestListener {

	private Map<String, String> allParameters = new HashMap<>();
	private Map<String, String> suiteParameters = new HashMap<>();
	private Map<String, String> localParameters = new HashMap<>();
	private List<String> fileList = new ArrayList<>();
	public static String testName = "";
	private HashMap<String, int[]> getResultsForBDDUpdate;
	HashMap<String, List<HashMap<String, String>>> statusResultForCategoryResults;

	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

	@Override
	public void onStart(ITestContext iTestContext) {
		allParameters = iTestContext.getSuite().getXmlSuite().getAllParameters();
		suiteParameters = iTestContext.getSuite().getXmlSuite().getParameters();
		localParameters = iTestContext.getCurrentXmlTest().getLocalParameters();
	}

	public Map<String, String> getAllParameters() {
		return allParameters;
	}

	public Map<String, String> getSuiteParameters() {
		return suiteParameters;
	}

	public Map<String, String> getLocalParameters() {
		return localParameters;
	}
	
	@Override
	public void onTestStart(ITestResult iTestResult) {
		testName = iTestResult.getParameters()[0].toString().replaceAll("\"", "");
		ExtentTestManager.startTest(iTestResult.getParameters()[0].toString().replaceAll("\"", ""),
				iTestResult.getParameters()[1].toString().replaceAll("\"", ""));

	}

	@Override
	public void onFinish(ITestContext iTestContext) {
		if (AbstractTestNGCucumberTest.tagsInScenario.size() > 0) {
			HashMap<String, List<String>> getTagPair = AbstractTestNGCucumberTest.tagsInScenario;
			getResultsForBDDUpdate = getBDDReportData(getTagPair, iTestContext);
		}

		ExtentConfiguration.getInstance().flush();
		ExtentTestManager.endTest();
		compressDirectory("AutomationReports", "AutomationReports.zip");
		updateBDDReportData(getResultsForBDDUpdate);
	}

	private void updateBDDReportData(HashMap<String, int[]> data) {
		try {
			String tableToBeUpdatedInReport = tableForReport(data);
			String categoryToUpdated = categoryReport(data);
			File bddReport = new File(".//BNYM_BDD_Reports/BDDExtentReport.html");
			Document doc = Jsoup.parse(bddReport, "ISO-8859-1");
			Elements table = doc.getElementsByAttributeValueMatching("class", "col s6").get(0)
					.getElementsByTag("table");
			for (Element tab : table) {
				// tab.replaceWith(new Element(tableToBeUpdatedInReport));
				tab.html(tableToBeUpdatedInReport);
			}
			Elements ullist = doc.getElementsByAttributeValueMatching("class", "category-collection").get(0)
					.getElementsByTag("ul");
			for (Element list : ullist) {
				// tab.replaceWith(new Element(tableToBeUpdatedInReport));
				ullist.html(categoryToUpdated);
			}

			FileWriter writer = new FileWriter(".//BNYM_BDD_Reports/BDDExtentReport.html");
			writer.write(doc.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String tableForReport(HashMap<String, int[]> data) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tr><th>Name</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Passed %</th></tr>");
		for (Map.Entry<String, int[]> entry : data.entrySet()) {
			sb.append("<tr>");
			String tempKey = entry.getKey();
			sb.append("<td>" + tempKey + "</td>");
			int[] tempvalue = entry.getValue();
			for (int i = 1; i < tempvalue.length; i++) {
				sb.append("<td>" + tempvalue[i] + "</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</tbody>");
		sb.append("</table>");
		return sb.toString();
	}

	private String categoryReport(HashMap<String, int[]> data) {
		StringBuffer categoryReport = new StringBuffer();
		categoryReport.append("<ul id='category-collection' class='category-collection'>");
		for (Map.Entry<String, int[]> entry : data.entrySet()) {
			int[] tempvalue = entry.getValue();
			categoryReport.append("<li class='category displayed active'>");
			categoryReport.append("<div class='category-heading'>");
			categoryReport.append("<span class='category-name'>" + entry.getKey() + "</span>");
			categoryReport.append("<span class='category-status right'>");
			if (tempvalue[1] > 0)
				categoryReport.append("<span class='label pass'>" + tempvalue[1] + "</span>");
			if (tempvalue[2] > 0)
				categoryReport.append("<span class='label fail'>" + tempvalue[2] + "</span>");
			if (tempvalue[3] > 0)
				categoryReport.append("<span class='label others'>" + tempvalue[3] + "</span>");
			categoryReport.append("</span>");
			categoryReport.append("</div>");
			categoryReport.append("<div class='category-content hide'>");
			categoryReport.append("<div class='category-status-counts'>");
			if (tempvalue[1] > 0)
				categoryReport.append("<span status='pass' class='label green accent-4 white-text'>Passed: "
						+ tempvalue[1] + "</span>");
			if (tempvalue[2] > 0)
				categoryReport.append("<span status='fail' class='label red lighten-1 white-text'>Failed: "
						+ tempvalue[2] + "</span>");
			if (tempvalue[3] > 0)
				categoryReport.append("<span status='skip' class='label yellow darken-2 white-text'>Skipped: "
						+ tempvalue[3] + "</span>");
			categoryReport.append("</div>");
			categoryReport.append("<div class='category-tests'>");
			categoryReport.append("<table class='bordered table-results'>");
			categoryReport.append("<thead>");
			categoryReport.append("<tr>");
			categoryReport.append("<th>TestName</th>");
			categoryReport.append("<th>Status</th>");
			categoryReport.append("</tr>");
			categoryReport.append("</thead>");
			categoryReport.append("<tbody>");
			List<HashMap<String, String>> details = statusResultForCategoryResults.get(entry.getKey());
			for (HashMap<String, String> getDetails : details) {
				for (Map.Entry<String, String> set : getDetails.entrySet()) {
					categoryReport.append("<tr>");
					categoryReport.append("<td style='white-space:nowrap;' class='linked' test-id='"
							+ RandomStringUtils.randomNumeric(2) + "'>" + set.getKey() + "</td>");
					if (set.getValue().equalsIgnoreCase("Pass")) {
						categoryReport.append("<td><span class='test-status " + set.getValue() + "'><font "
								+ "color='#00c853'>" + set.getValue() + "</font></span></td>");
					} else if (set.getValue().equalsIgnoreCase("Fail")) {
						categoryReport.append("<td><span class='test-status " + set.getValue() + "'><font "
								+ "color='#EF5350'>" + set.getValue() + "</font></span></td>");
					}
					categoryReport.append("</tr>");
				}
			}
			categoryReport.append("</tbody>");
			categoryReport.append("</table>");
			categoryReport.append("</div>");
			categoryReport.append("</div>");
			categoryReport.append("</li>");
		}
		categoryReport.append("</ul>");

		return categoryReport.toString();
	}

	
	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		Log.info(testName + " passed successfully!!");
	}	

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		Log.error(testName + " failed");
		/*
		 * if (ExtentTestManager.getTest() != null) { if
		 * (iTestResult.getThrowable().toString().contains("java.lang.AssertionError"))
		 * { String errMsg = iTestResult.getThrowable().getMessage(); try {
		 * ExtentTestManager.getTest().log(Status.FAIL, "On test failure : " +
		 * errMsg.substring(0, errMsg.indexOf("expected") - 1).trim()); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * } else { ExtentTestManager.getTest().log(Status.FAIL, "Test Step Failed: " +
		 * iTestResult.getThrowable()); } }
		 */

	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		if (ExtentTestManager.getTest() != null)
			ExtentTestManager.getTest().log(Status.SKIP, iTestResult.getName() + " execution got skipped.");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		Log.info("");
	}

	private void compressDirectory(String dir, String zipFile) {
		File directory = new File(dir);
		getFileList(directory);
		try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (String filePath : fileList) {
				// Creates a zip entry.
				String name = filePath.substring(directory.getAbsolutePath().length() + 1, filePath.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zos.putNextEntry(zipEntry);
				// Read file content and write to zip output stream.
				try (FileInputStream fis = new FileInputStream(filePath)) {
					byte[] buffer = new byte[1024];
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					// Close the zip entry.
					zos.closeEntry();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteFolder(String deleteFolderPath) {
//		File file = new File(deleteFolderPath);
//		try {
//			FileUtils.deleteDirectory(file);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * Get files list from the directory recursive to the sub directory.
	 */
	private void getFileList(File directory) {
		File[] files = directory.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					fileList.add(file.getAbsolutePath());
				} else {
					getFileList(file);
				}
			}
		}
	}

	public static void killDrivers() {
		String jdkVersion = System.getProperty("sun.arch.data.model");
		if (ConfigProvider.getAsString("browser").equalsIgnoreCase("ie")) {

			try {

				Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer_" + jdkVersion + "bit_3.12.exe");
				Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (ConfigProvider.getAsString("browser").equalsIgnoreCase("chrome")) {
			/*
			 * int browserVersion = ConfigProvider.getAsInt("chrome.version"); int version =
			 * 0; if (browserVersion <= 64) { version = 35; } else if (browserVersion <= 66)
			 * { version = 37; } else if (browserVersion <= 68) { version = 40; } else if
			 * (browserVersion <= 70) { version = 44; } else if (browserVersion >= 71) {
			 * version = 45; }
			 */

			try {
				Runtime.getRuntime().exec("taskkill /F /IM chromedriver_2.46.exe");
				// Runtime.getRuntime().exec("taskkill /F /IM chromedriver_2."+version+".exe");
				// Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> getTestResult(Set<ITestResult> resultset) {
		ArrayList<String> testResult = new ArrayList<>();
		for (ITestResult res : resultset) {
			testResult.add(res.getParameters()[0].toString().replaceAll("\"", ""));
		}
		return testResult;
	}

	private HashMap<String, int[]> getBDDReportData(HashMap<String, List<String>> getTagPair,
			ITestContext iTestContext) {
		ArrayList<String> passedResult = getTestResult(iTestContext.getPassedTests().getAllResults());
		ArrayList<String> failedResult = getTestResult(iTestContext.getFailedTests().getAllResults());
		ArrayList<String> skippedResult = getTestResult(iTestContext.getSkippedTests().getAllResults());
		int passCount, failCount, skipCount;
		HashMap<String, int[]> tagResults = new HashMap<>();
		statusResultForCategoryResults = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : getTagPair.entrySet()) {
			HashMap<String, String> scenarioResults = new HashMap<>();
			passCount = 0;
			failCount = 0;
			skipCount = 0;
			String tempKey = entry.getKey();
			if (passedResult.contains(tempKey)) {
				passCount += 1;
				scenarioResults.put(tempKey, "Pass");
			} else if (failedResult.contains(tempKey)) {
				failCount += 1;
				scenarioResults.put(tempKey, "Fail");
			} else if (skippedResult.contains(tempKey)) {
				skipCount += 1;
				scenarioResults.put(tempKey, "Skip");
			}
			List<String> tempValue = entry.getValue();
			for (String tagVal : tempValue) {
				if (statusResultForCategoryResults.containsKey(tagVal)) {
					List<HashMap<String, String>> temp = statusResultForCategoryResults.get(tagVal);
					temp.add(scenarioResults);
					statusResultForCategoryResults.put(tagVal, temp);
				} else {
					List<HashMap<String, String>> temp = new ArrayList<>();
					temp.add(scenarioResults);
					statusResultForCategoryResults.put(tagVal, temp);
				}

				if (tagResults.isEmpty()) {
					tagResults.put(tagVal, new int[] { 0, passCount, failCount, skipCount, 0 });
				} else if (tagResults.containsKey(tagVal)) {
					int[] updatedArray;
					updatedArray = tagResults.get(tagVal);
					updatedArray[0] = 0;
					updatedArray[1] += passCount;
					updatedArray[2] += failCount;
					updatedArray[3] += skipCount;
					updatedArray[4] = 0;
					tagResults.put(tagVal, updatedArray);
				} else {
					tagResults.put(tagVal, new int[] { 0, passCount, failCount, skipCount, 0 });
				}
			}
		}		
		for (Map.Entry<String, int[]> tempentry : tagResults.entrySet()) {
			String tempKey = tempentry.getKey();
			int[] tempValue = tempentry.getValue();
			int totalCount = IntStream.of(tempValue).sum();
			tempValue[0] = totalCount;
			tempValue[4] = (tempValue[1] * 100) / totalCount;
			tagResults.put(tempKey, tempValue);
		}
		//Log.info("getBddReportInfo :-->"+tagResults);
		return tagResults;
	}

}