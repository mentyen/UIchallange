package com.rebar.utilities.extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.rebar.utilities.configprovider.ConfigProvider;
import com.rebar.utilities.configprovider.exceptions.PropertyFileNotFoundException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ExtentConfiguration {
    private static ExtentReports extent;
    public static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String TIME_STAMP = (new SimpleDateFormat("dd.MM.yyyy.HH.mm")).format(new Date());
    private static final String EXTENT_REPORTS_FOLDER;
    private static final String REPORT_NAME;
    private static final String EXTENT_REPORTS_PATH;
    private static Logger logger;

    private ExtentConfiguration() {
    }

    public static String getExtentReportsFolder() {
        return EXTENT_REPORTS_PATH;
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            createReportsFolder();
            attachReporters();
        }

        return extent;
    }

    private static void createReportsFolder() {
        File file = new File(EXTENT_REPORTS_FOLDER);
        if (!file.exists() && !file.mkdir()) {
            logger.warning("Failed to create directory!");
        }

    }

    private static ExtentSparkReporter initHtmlReporter() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(EXTENT_REPORTS_PATH);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle(REPORT_NAME);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("Execution-Status");
        htmlReporter.config().setCss("css-string");
        htmlReporter.config().setJs("js-string");
        htmlReporter.config().setProtocol(Protocol.HTTPS);
        htmlReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        htmlReporter.config().setTimelineEnabled(true);
        return htmlReporter;
    }

    private static ExtentKlovReporter initKlovReporter() {
        ExtentKlovReporter klovReporter = new ExtentKlovReporter(ConfigProvider.getAsString("project.name"), ConfigProvider.getAsString("report.name"));
        klovReporter.initMongoDbConnection(ConfigProvider.getAsString("mongodb.host"), ConfigProvider.getAsInt("mongodb.port"));
        klovReporter.initKlovServerConnection(ConfigProvider.getAsString("klov.host"));
        return klovReporter;
    }

    private static ExtentReports attachReporters() {
        String klovReporterRequired = null;
        String ExetentReporterRequired = null;
        extent = new ExtentReports();

        try {
            ExetentReporterRequired = System.getProperty("ExtentReport", ConfigProvider.getAsString("ExtentReport"));
        } catch (PropertyFileNotFoundException var4) {
            var4.printStackTrace();
        }

        if (ExetentReporterRequired != null && (ExetentReporterRequired.equalsIgnoreCase("true") || ExetentReporterRequired.equalsIgnoreCase("yes"))) {
            extent.attachReporter(new ExtentObserver[]{initHtmlReporter()});
        } else if (ExetentReporterRequired == null || ExetentReporterRequired == "") {
            extent.attachReporter(new ExtentObserver[]{initHtmlReporter()});
        }

        try {
            klovReporterRequired = System.getProperty("KlovReport", ConfigProvider.getAsString("KlovReport"));
        } catch (PropertyFileNotFoundException var3) {
            return extent;
        }

        if (klovReporterRequired != null && (klovReporterRequired.equalsIgnoreCase("true") || klovReporterRequired.equalsIgnoreCase("yes"))) {
            extent.attachReporter(new ExtentObserver[]{initKlovReporter()});
        }

        return extent;
    }

    static {
        EXTENT_REPORTS_FOLDER = WORKING_DIR + "/AutomationReports";
        String var10000 = TIME_STAMP;
        REPORT_NAME = "ExtentReport_" + var10000 + "_" + Thread.currentThread().getId() + ".html";
        EXTENT_REPORTS_PATH = EXTENT_REPORTS_FOLDER + File.separator + REPORT_NAME;
        logger = Logger.getLogger(com.rebar.utilities.extentreports.ExtentConfiguration.class.getName());
    }
}

