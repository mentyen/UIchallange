package com.cucumber.utilities;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.rebar.utilities.Log;
import com.rebar.utilities.extentreports.ExtentTestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshots {

	public static String SCREENSHOTS_FOLDER_PATH;
	private static final String SCREENSHOTS_FOLDER = "/AutomationReports/screenshots/";
	private static Robot robot;

	static {
		createDirectory();
	}

	protected static String captureScreenshot(WebDriver driver, String screenshotName) {

		String randomNumber = RandomStringUtils.randomNumeric(5);
		String destinationPath = "." + SCREENSHOTS_FOLDER + screenshotName + randomNumber + ".png";
		TakesScreenshot ts = (TakesScreenshot) driver;
		File srcFile = ts.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File(destinationPath));
		} catch (IOException e) {
			Log.warn("Not able to capture screenshot");
		}

		return destinationPath;
	}

	private static void createDirectory() {
		String drive = geDriveWithFreeSpace();
		if (drive.contains("C:")) {
			SCREENSHOTS_FOLDER_PATH = System.getProperty("user.dir") + SCREENSHOTS_FOLDER;
		} else {
			SCREENSHOTS_FOLDER_PATH = drive + SCREENSHOTS_FOLDER;
		}
		if (!new File(SCREENSHOTS_FOLDER_PATH).exists()) {
			File file = new File(SCREENSHOTS_FOLDER_PATH);
			if (!file.exists()) {
				if (!file.mkdir()) {
					Log.warn("Failed to create directory!");
				}
			}
		}
	}

	public static void addStepWithScreenshotInReport(WebDriver driver, String message) {
		if (driver != null) {
			String path = "." + Screenshots.captureScreenshot(driver, "screenshot");
			try {
				ExtentTestManager.getTest().info(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.warn("driver is null in addStepWithScreenshotInReport()");
			ExtentTestManager.getTest().info(message);
		}
	}

	public static void addFailStepWithScreenshotInReport(WebDriver driver, String message) {
		if (driver != null) {
			String path = "." + Screenshots.captureScreenshot(driver, "screenshot");
			try {
				ExtentTestManager.getTest().fail(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.warn("driver is null in addStepWithScreenshotInReport()");
			ExtentTestManager.getTest().info(message);
		}
	}

	public static void addWarningStepWithScreenshotInReport(WebDriver driver, String message) {
		if (driver != null) {
			String path = "." + Screenshots.captureScreenshot(driver, "screenshot");
			try {
				ExtentTestManager.getTest().warning(message,
						MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ExtentTestManager.getTest().pass(message);
		}
	}

	public static void addStepInReport(String message) {
		ExtentTestManager.getTest().info(message);
	}

	private static String geDriveWithFreeSpace() {
		String driveWithFreeSpace = null;
		File[] availableDrives = File.listRoots();
		if (availableDrives.length >= 1) {
			for (File file : availableDrives) {
				if (file.getFreeSpace() > 100000000) {
					driveWithFreeSpace = file.toString();
					break;
				}
			}
		}
		return driveWithFreeSpace;
	}

	public static void addStepWithScreenshotInReportMainFrame(String message) {
		String path = Screenshots.captureScreenshotMainFrame("screenshot");
		ExtentTestManager.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

	public static void addFailStepWithScreenshotInReportMainFrame(String message) {
		String path = Screenshots.captureScreenshotMainFrame("screenshot");
		ExtentTestManager.getTest().fail(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

	public static void addWarnStepWithScreenshotInReportMainFrame(String message) {
		String path = Screenshots.captureScreenshotMainFrame("screenshot");
		ExtentTestManager.getTest().warning(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

	/**
	 * Captures the desktop screen for mainframe
	 * 
	 * @param screenshotName
	 * @return
	 * 
	 */

	protected static String captureScreenshotMainFrame(String screenshotName) {

		String randomNumber = RandomStringUtils.randomNumeric(5);
		String destinationPath = SCREENSHOTS_FOLDER_PATH + screenshotName + randomNumber + ".png";

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unused")
		DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]
				.getDisplayMode();
		Rectangle c1 = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Rectangle c2 = new Rectangle(0, 60, (c1.width / 3) - 20, (c1.height / 2) - 70);
		BufferedImage screenShot = robot.createScreenCapture(c2);

		try {
			ImageIO.write(screenShot, "png", new File(destinationPath));
		} catch (IOException e) {
			logFail("Not able to capture screenshot, as result exception accure:-->" + e.getMessage(), false);

		}

		return destinationPath;
	}

	/**
	 * Captures the desktop screen for sbclient
	 * 
	 * @param screenshotName
	 * @return
	 * 
	 */

	protected static String captureScreenshot(String screenshotName) {

		String randomNumber = RandomStringUtils.randomNumeric(5);
		String destinationPath = SCREENSHOTS_FOLDER_PATH + screenshotName + randomNumber + ".png";

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		BufferedImage screenShot = robot
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		try {
			ImageIO.write(screenShot, "png", new File(destinationPath));
		} catch (IOException e) {
			logFail("Not able to capture screenshot, as result exception accure:-->" + e.getMessage(), false);

		}

		return destinationPath;
	}

	protected static void logFail(Object msg, boolean isScreenshotRequire) {
		if (!isScreenshotRequire) {
			ExtentTestManager.getTest().fail(String.valueOf(msg));
			Log.warn(String.valueOf(msg));
		} else {
			Screenshots.addStepWithScreenshotInReport(String.valueOf(msg));
		}
	}

	public static void addStepWithScreenshotInReport(String message) {
		String path = Screenshots.captureScreenshot("screenshot");
		ExtentTestManager.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

}
