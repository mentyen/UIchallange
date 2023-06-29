package com.rebar.utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.rebar.utilities.extentreports.ExtentTestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.log4testng.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Screenshots {
    private static final Logger logger = Logger.getLogger(com.rebar.utilities.Screenshots.class);
    private static String screenshotsFolderPath;
    private static final String SCREENSHOTS_FOLDER = "\\screenshots\\";

    private Screenshots() {
    }

    public static String getScreenshotsFolderPath() {
        return screenshotsFolderPath;
    }

    private static void createDirectory() {
        String drive = geDriveWithFreeSpace();
        if (drive != null && drive.contains("C:")) {
            screenshotsFolderPath = FileUtils.getTempDirectoryPath() + "\\screenshots\\";
        } else {
            screenshotsFolderPath = drive + "\\screenshots\\";
        }

        screenshotsFolderPath = "AutomationReports/screenshots/";
        File file = new File(screenshotsFolderPath);
        if (!file.exists() && !file.mkdir()) {
            logger.warn("Failed to create directory!");
        }

    }

    public static void addStepWithScreenshotInReport(WebDriver driver, String message) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
        if (extentTest != null) {
            if (driver != null) {
                String path = captureScreenshot(driver, "screenshot");

                try {
                    extentTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            } else {
                extentTest.pass(message);
            }
        }

    }

    public static void addStepInReport(String message) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
        if (extentTest != null) {
            extentTest.pass(message);
        }

    }

    public static void addStepInReport(boolean condition, String message) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
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
            File[] var2 = availableDrives;
            int var3 = availableDrives.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File file = var2[var4];
                if (file.getFreeSpace() > 100000000L) {
                    driveWithFreeSpace = file.toString();
                    break;
                }
            }
        }

        return driveWithFreeSpace;
    }

    protected static String captureDesktop(String screenshotName) throws AWTException {
        String randomNumber = RandomStringUtils.randomNumeric(5);
        String destinationPath = screenshotsFolderPath + screenshotName + randomNumber + ".png";
        Robot r = new Robot();
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage Image = r.createScreenCapture(capture);

        try {
            ImageIO.write(Image, "png", new File(destinationPath));
        } catch (IOException var7) {
            logger.warn("Not able to capture desktop");
        }

        return destinationPath;
    }

    public static void addStepWithDesktopScreenInReport(String message) throws Exception {
        String path = captureDesktop("screenshot");
        com.rebar.utilities.extentreports.ExtentTestManager.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
    }

    public static void addFailureStepWithScreenshotInReport(WebDriver driver, String message) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
        if (extentTest != null) {
            if (driver != null) {
                String path = captureScreenshot(driver, "screenshot");

                try {
                    extentTest.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
                } catch (Exception var5) {
                    logger.warn(var5.getMessage());
                }
            } else {
                extentTest.fail(message);
            }
        }

    }

    public static void addJSONoutputToReport(WebDriver driver, String jsonString, com.rebar.utilities.Screenshots.Status status) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
        Markup m = MarkupHelper.createCodeBlock(jsonString, CodeLanguage.JSON);
        if (status.equals(com.rebar.utilities.Screenshots.Status.PASS)) {
            extentTest.pass(m);
        } else {
            extentTest.pass(m);
        }

    }

    public static void addXMLoutputToReport(WebDriver driver, String jsonString, com.rebar.utilities.Screenshots.Status status) {
        ExtentTest extentTest = com.rebar.utilities.extentreports.ExtentTestManager.getTest();
        Markup m = MarkupHelper.createCodeBlock(jsonString, CodeLanguage.XML);
        if (status.equals(com.rebar.utilities.Screenshots.Status.PASS)) {
            extentTest.pass(m);
        } else {
            extentTest.pass(m);
        }

    }

    public static void addTableOutputToReport(WebDriver driver, String[][] tableData, com.rebar.utilities.Screenshots.Status status) {
        ExtentTest extentTest = ExtentTestManager.getTest();
        Markup m = MarkupHelper.createTable(tableData);
        if (status.equals(com.rebar.utilities.Screenshots.Status.PASS)) {
            extentTest.pass(m);
        } else {
            extentTest.pass(m);
        }

    }

    protected static String captureScreenshot(WebDriver driver, String screenshotName) {
        String randomNumber = RandomStringUtils.randomNumeric(5);
        String destinationPath = screenshotsFolderPath + screenshotName + randomNumber + ".png";
        TakesScreenshot ts = (TakesScreenshot)driver;
        File srcFile = (File)ts.getScreenshotAs(OutputType.FILE);

        try {
            updatetimestamp(srcFile, destinationPath);
        } catch (IOException var7) {
            logger.warn("Not able to capture screenshot");
        }

        return destinationPath.substring(destinationPath.indexOf("/") + 1);
    }

    public static void updatetimestamp(File SourceFilename, String TargetFilename) throws IOException {
        BufferedImage image = ImageIO.read(SourceFilename);
        SimpleDateFormat formatter = new SimpleDateFormat();
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(18.0F));
        g.setColor(new Color(255, 20, 20));
        Date date = new Date(System.currentTimeMillis());
        g.drawString(formatter.format(date), image.getWidth() - 250, image.getHeight() - 20);
        g.dispose();
        ImageIO.write(image, "png", new File(TargetFilename));
    }

    static {
        createDirectory();
    }

    public static enum Status {
        PASS,
        FAIL;

        private Status() {
        }
    }
}
