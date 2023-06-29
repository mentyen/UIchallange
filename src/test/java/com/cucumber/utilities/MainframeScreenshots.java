package com.cucumber.utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.rebar.utilities.extentreports.ExtentTestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.log4testng.Logger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainframeScreenshots {

	private static final Logger logger = Logger.getLogger(MainframeScreenshots.class);
	private static String screenshotsFolderPath;
	private static final String SCREENSHOTS_FOLDER = "\\screenshots\\";

	private MainframeScreenshots() {
	}

	static {
		createDirectory();
	}

	public static String getScreenshotsFolderPath() {
		return screenshotsFolderPath;
	}

	private static void createDirectory() {
		String drive = geDriveWithFreeSpace();
		if (drive != null && drive.contains("C:")) {
			screenshotsFolderPath = FileUtils.getTempDirectoryPath() + SCREENSHOTS_FOLDER;
		} else {
			screenshotsFolderPath = drive + SCREENSHOTS_FOLDER;
		}
		screenshotsFolderPath = "AutomationReports/screenshots/";
		File file = new File(screenshotsFolderPath);
		if (!file.exists() && !file.mkdir()) {
			logger.warn("Failed to create directory!");
		}
	}

	/**
	 * Adds a step in the report with the specified string message without
	 * screenshot. Useful for steps which does not involve UI(Ex: API testing)
	 * 
	 * @param message
	 */
	public static void addStepInReport(String message) {
		ExtentTest extentTest = ExtentTestManager.getTest();
		if (extentTest != null)
			extentTest.pass(message);

	}

	/**
	 * Adds a step in the report with the specified string message & condition
	 * based result without screenshot. Useful for steps which does not to skip
	 * the step due to assertion failure
	 * 
	 * @param message
	 */
	public static void addStepInReport(boolean condition, String message) {
		ExtentTest extentTest = ExtentTestManager.getTest();
		if (extentTest != null) {
			if (condition) {
				extentTest.pass(message);

			} else {
				extentTest.fail(message);

			}
		}
	}

	private static String geDriveWithFreeSpace() {
		String driveWithFreeSpace = null;
		File[] availableDrives = File.listRoots();
		if (availableDrives.length > 1) {
			for (File file : availableDrives) {
				if (file.getFreeSpace() > 100000000) {
					driveWithFreeSpace = file.toString();
					break;
				}
			}
		}
		return driveWithFreeSpace;
	}

	/**
	 * Captures the desktop screen
	 * 
	 * @param screenshotName
	 * @return
	 * @throws AWTException
	 */
	protected static String captureDesktop(String screenshotName) throws AWTException {

		String randomNumber = RandomStringUtils.randomNumeric(5);
		String destinationPath = screenshotsFolderPath + screenshotName + randomNumber + ".png";
		Robot r = new Robot();

		@SuppressWarnings("unused")
		DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]
				.getDisplayMode();
		Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Rectangle capture2 = new Rectangle(0, 60, (capture.width / 3) - 20, (capture.height / 2) - 70);
		BufferedImage Image = r.createScreenCapture(capture2);
		try {
			ImageIO.write(Image, "png", new File(destinationPath));
		} catch (IOException e) {
			logger.warn("Not able to capture desktop");
		}
		return destinationPath;
	}

	public static void addStepWithDesktopScreenInReport(String message) throws Exception {
		String path = MainframeScreenshots.captureDesktop("screenshot");
		path = path.replace("AutomationReports/", "");
		ExtentTestManager.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());		

	}

}
