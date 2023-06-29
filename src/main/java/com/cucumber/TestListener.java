package com.cucumber;


import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.rebar.utilities.extentreports.ExtentConfiguration;
import com.rebar.utilities.extentreports.ExtentTestManager;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static Logger logger = LogManager.getLogger(TestListener.class.getName());
    private Map<String, String> allParameters = new HashMap();
    private Map<String, String> suiteParameters = new HashMap();
    private Map<String, String> localParameters = new HashMap();
    private List<String> fileList = new ArrayList();

    public TestListener() {
    }

    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    public void onStart(ITestContext iTestContext) {
        this.allParameters = iTestContext.getSuite().getXmlSuite().getAllParameters();
        this.suiteParameters = iTestContext.getSuite().getXmlSuite().getParameters();
        this.localParameters = iTestContext.getCurrentXmlTest().getLocalParameters();
    }

    public Map<String, String> getAllParameters() {
        return this.allParameters;
    }

    public Map<String, String> getSuiteParameters() {
        return this.suiteParameters;
    }

    public Map<String, String> getLocalParameters() {
        return this.localParameters;
    }

    public void onFinish(ITestContext iTestContext) {
        ExtentConfiguration.getInstance().flush();
        ExtentTestManager.endTest();
        this.compressDirectory("AutomationReports", "AutomationReports.zip");
    }

    public void onTestStart(ITestResult iTestResult) {
        ExtentTestManager.startTest(iTestResult.getParameters()[0].toString().replaceAll("\"", ""), iTestResult.getParameters()[1].toString().replaceAll("\"", ""));
    }

    public void onTestSuccess(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " passed successfully!!");
    }

    public void onTestFailure(ITestResult iTestResult) {
        logger.warn(getTestMethodName(iTestResult) + " failed");
        if (ExtentTestManager.getTest() != null) {
//            if (iTestResult.getThrowable().toString().contains("java.lang.AssertionError")) {
//                String errMsg = iTestResult.getThrowable().getMessage();
//                try {
//                    ExtentTestManager.getTest().log(Status.FAIL, "Test Step Failed due to following error: " + errMsg.substring(0, errMsg.indexOf("expected") - 1).trim(), MediaEntityBuilder.createScreenCaptureFromPath(takeScreenShot()).build());
//                } catch (Exception var4) {
//                    var4.printStackTrace();
//                }
//            } else {
//                ExtentTestManager.getTest().log(Status.FAIL, "Test Step Failed: " + iTestResult.getThrowable());
//            }
        }

    }

    protected static String takeScreenShot() {
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime());
        String imageName = "c:\\temp\\" + timeStamp + ".png";
        BufferedImage image = null;

        try {
            image = (new Robot()).createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (AWTException | HeadlessException var5) {
            var5.printStackTrace();
        }

        try {
            ImageIO.write(image, "png", new File(imageName));
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return imageName;
    }

    public void onTestSkipped(ITestResult iTestResult) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.SKIP, iTestResult.getName() + " execution got skipped.");
        }

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        logger.info("");
    }

    private void compressDirectory(String dir, String zipFile) {
        File directory = new File(dir);
        this.getFileList(directory);

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);

            try {
                ZipOutputStream zos = new ZipOutputStream(fos);

                try {
                    Iterator var6 = this.fileList.iterator();

                    while(var6.hasNext()) {
                        String filePath = (String)var6.next();
                        logger.info("Compressing: " + filePath);
                        String name = filePath.substring(directory.getAbsolutePath().length() + 1, filePath.length());
                        ZipEntry zipEntry = new ZipEntry(name);
                        zos.putNextEntry(zipEntry);

                        try {
                            FileInputStream fis = new FileInputStream(filePath);

                            try {
                                byte[] buffer = new byte[1024];

                                while(true) {
                                    int length;
                                    if ((length = fis.read(buffer)) <= 0) {
                                        zos.closeEntry();
                                        break;
                                    }

                                    zos.write(buffer, 0, length);
                                }
                            } catch (Throwable var16) {
                                try {
                                    fis.close();
                                } catch (Throwable var15) {
                                    var16.addSuppressed(var15);
                                }

                                throw var16;
                            }

                            fis.close();
                        } catch (Exception var17) {
                            var17.printStackTrace();
                        }
                    }
                } catch (Throwable var18) {
                    try {
                        zos.close();
                    } catch (Throwable var14) {
                        var18.addSuppressed(var14);
                    }

                    throw var18;
                }

                zos.close();
            } catch (Throwable var19) {
                try {
                    fos.close();
                } catch (Throwable var13) {
                    var19.addSuppressed(var13);
                }

                throw var19;
            }

            fos.close();
        } catch (IOException var20) {
            var20.printStackTrace();
        }

    }

    private void getFileList(File directory) {
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.isFile()) {
                    this.fileList.add(file.getAbsolutePath());
                } else {
                    this.getFileList(file);
                }
            }
        }

    }
}
