package com.cucumber.utilities;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Map;
import java.util.Set;

public class AssertionLibrary {
	private static WebDriver driver;
	private static SoftAssert softAssert = null;
	public AssertionLibrary(WebDriver driver) {
		AssertionLibrary.driver = driver;
	}

	public enum Screenshot {
		REQUIRED, NOT_REQUIRED;
	}

	/**
	 * It will work as TestNG assertEquals assertion.
	 * 
	 * @see //Assert.assertEquals(actual, expected, message);
	 * @param actual
	 * @param expected
	 * @param message
	 */
	public static void assertEquals(String actual, String expected, String message, Screenshot screenshot) {
		AssertionLibrary.assertEquals((Object) actual, (Object) expected, message, screenshot);
	}

	public static void assertEquals(String actual, String expected, String message) {
		AssertionLibrary.assertEquals((Object) actual, (Object) expected, message, Screenshot.REQUIRED);
	}

	public static void assertEquals(Double actual, Double expected, Double delta, String message,
			Screenshot screenshot) {
		String reportMessage = message + "<br> Actual: " + actual.toString() + "<br> Expected: " + expected.toString();
		Assert.assertEquals(actual, expected, delta, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertEquals(Double actual, Double expected, Double delta, String message) {
		assertEquals(actual, expected, delta, message, Screenshot.REQUIRED);
	}

	public static void assertEquals(Object actual, Object expected, String reportMessage, Screenshot screenshot) {
		Assert.assertEquals(actual, expected, reportMessage);
		//attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertEquals(Object actual, Object expected, String message) {
		assertEquals(actual, expected, message, Screenshot.REQUIRED);
	}

	/**
	 * It will work as TestNG assertTrue assertion.
	 * 
	 * @see //Assert.assertTrue(condition, message);
	 * @param //actual
	 * @param //expected
	 * @param //message
	 */
	public static void assertTrue(boolean condition, String message, Screenshot screenshot) {
		String reportMessage = message;
		attachScreenshotIfRequired(screenshot, reportMessage);		
		Assert.assertTrue(condition, message);
	}

	public static void assertTrue(boolean condition, String message) {
		assertTrue(condition, message, Screenshot.REQUIRED);
	}

	public static void assertTrue(boolean condition, String message, Boolean isScreenshotRequire) {
		if (!isScreenshotRequire)
			assertTrue(condition, message, Screenshot.NOT_REQUIRED);
		else
			assertTrue(condition, message, Screenshot.REQUIRED);
	}

	public static void softAssertTrue(boolean condition, String message, Screenshot screenshot) {
		softAssert = new SoftAssert();
		String reportMessage = message + "<br> Condition: " + condition;
		softAssert.assertTrue(condition, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertAll() {
		softAssert.assertAll();
	}

	/**
	 * It will work as TestNG assertNotEquals assertion.
	 * 
	 * @see //Assert.assertNotEquals(actual, expected, message);
	 * @param //actual
	 * @param //expected
	 * @param message
	 */
	public static void assertNotEquals(String actual1, String actual2, String message, Screenshot screenshot) {
		assertNotEquals((Object) actual1, (Object) actual2, message, screenshot);
	}

	public static void assertNotEquals(String actual1, String actual2, String message) {
		assertNotEquals((Object) actual1, (Object) actual2, message, Screenshot.REQUIRED);
	}

	public static void assertNotEquals(Double actual1, Double actual2, Double delta, String message,
			Screenshot screenshot) {
		String reportMessage = message + "<br> Actual: " + actual1.toString() + "<br> Expected: " + actual2.toString();
		Assert.assertNotEquals(actual1, actual2, delta, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertNotEquals(Double actual1, Double actual2, Double delta, String message) {
		assertNotEquals(actual1, actual2, delta, message, Screenshot.REQUIRED);
	}

	public static void assertNotEquals(Object actual1, Object actual2, String message, Screenshot screenshot) {
		String reportMessage = message + "<br> Actual: " + actual1.toString() + "<br> Expected: " + actual2.toString();
		Assert.assertNotEquals(actual1, actual2, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertNotEquals(Object actual1, Object actual2, String message) {
		assertNotEquals(actual1, actual2, message, Screenshot.REQUIRED);
	}

	/**
	 * It will work as TestNG assertFalse assertion.
	 * 
	 * @see //Assert.assertFalse(condition, message);
	 * @param //actual
	 * @param //expected
	 * @param message
	 */
	public static void assertFalse(boolean condition, String message, Screenshot screenshot) {
		String reportMessage = message + "<br> Condition: " + condition;
		Assert.assertFalse(condition, message);
		//attachScreenshotIfRequired(screenshot, reportMessage);
		
	}

	public static void assertFalse(boolean condition, String message) {
		assertFalse(condition, message, Screenshot.REQUIRED);
	}

	public static void assertEquals(Set<?> actual, Set<?> expected, String message, Screenshot screenshot) {
		String reportMessage = message + "<br> Actual: " + actual.toString() + "<br> Expected: " + expected.toString();
		Assert.assertEquals(actual, expected, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertEquals(Set<?> actual, Set<?> expected, String message) {
		assertEquals(actual, expected, message, Screenshot.REQUIRED);
	}

	public static void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message, Screenshot screenshot) {
		String reportMessage = message + "<br> Actual: " + actual.toString() + "<br> Expected: " + expected.toString();
		Assert.assertEquals(actual, expected, message);
		attachScreenshotIfRequired(screenshot, reportMessage);
	}

	public static void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
		assertEquals(actual, expected, message, Screenshot.REQUIRED);
	}

	public static void attachScreenshotIfRequired(Screenshot screenshot, String message) {
		if (screenshot.equals(Screenshot.REQUIRED)) {
			Screenshots.addStepWithScreenshotInReport(driver, message);
		} else {
			Screenshots.addStepInReport(message);
		}
	}

}
