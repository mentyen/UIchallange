package com.rebar.utilities.extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ExtentTestManager {
    private static Map<Integer, ExtentTest> extentTestMap = new HashMap();
    private static Set<Integer> extentThreadList = new HashSet();
    private static ExtentReports extent;
    private static Logger logger = LogManager.getLogger(ExtentTestManager.class.getName());

    private ExtentTestManager() {
    }

    public static synchronized ExtentTest getTest() {
        return (ExtentTest)extentTestMap.get(getCurrentThread());
    }

    public static synchronized void endTest() {
        logger.info("Test end : [" + Thread.currentThread().getId()+"]");
        extentThreadList.remove(getCurrentThread());
        if (!extentTestMap.isEmpty() && extentThreadList.isEmpty()) {
            new HashSet();
            Set<Integer> s1 = extentTestMap.keySet();
            Iterator var1 = s1.iterator();

            while(var1.hasNext()) {
                Integer i = (Integer)var1.next();
                extent.removeTest((ExtentTest)extentTestMap.get(i));
            }
            //logger.info(extentTestMap);
        }

    }

    public static synchronized ExtentTest startTest(String testName, String desc) {
        logger.info("Test start : [" + Thread.currentThread().getId()+"]["+testName+"]");
        extent = ExtentConfiguration.getInstance();
        ExtentTest test = extent.createTest(testName, desc);
        extentTestMap.put(getCurrentThread(), test);
        extentThreadList.add(getCurrentThread());
        return test;
    }

    private static synchronized int getCurrentThread() {
        return (int)Thread.currentThread().getId();
    }
}

