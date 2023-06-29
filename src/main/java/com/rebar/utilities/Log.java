package com.rebar.utilities;

import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Log {
	//private static  Logger Log = LoggerFactory.getLogger(Log.class.getName());
	private static Logger Log = LogManager.getLogger(Log.class.getName());

	// run so many test cases as a test suite
	public static void startTestCase(String sTestCaseName) {
		Log.info("Scenario start : [" + sTestCaseName+ "]");
	}

	// This is to print log for the ending of the test case
	public static void endTestCase(String sTestCaseName) {
		Log.info("Scenario ends : [" + sTestCaseName+ "]");
	}

	public static void error(String message) {
		Log.error(message);
	}

	public static void error(String message, Throwable e) {
		Log.error(message + " Error: " + e.getMessage());
	}

	// Need to create these methods, so that they can be called
	public static void info(Object msg) {
		Log.info(String.valueOf(msg));
	}
	
	public static void warn(Object msg) {
		Log.warn(String.valueOf(msg));
	}

	public static void debug(Object msg) {
		Log.debug(String.valueOf(msg));
	}

	public static void debug(String message, Throwable e) {
		Log.debug(message, e);
	}

	public static void error(Exception e) {
		Log.error(String.valueOf(e));
	}

	public static void info(List<String> jobGroupNames) {
		Log.info(jobGroupNames.toString());
	}

	public static void info(boolean flag) {
		Log.info(String.valueOf(flag));
	}
}