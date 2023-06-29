package com.cucumber.utilities;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class StringUtils {

	private static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String NUMERIC = "01234567890";
	private static final String SPECIALS = "~!@#$%^&*()\\/|{}[]+=_-`<>,.?:;";

	public static boolean isBooleanTrue(String value) {
		if (value == null)
			return false;
		value = value.trim();

		return (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("select") || value.equalsIgnoreCase("check") || value.equalsIgnoreCase("tick")
				|| value.equalsIgnoreCase("on"));
	}

	public static boolean isBooleanFalse(String value) {
		if (value == null)
			return false;
		value = value.trim();

		return (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("unselect") || value.equalsIgnoreCase("uncheck")
				|| value.equalsIgnoreCase("untick") || value.equalsIgnoreCase("off"));
	}

	public static boolean isBoolean(String value) {
		return isBooleanTrue(value) || isBooleanFalse(value);
	}

	private static String cleanText(String text) {
		if (text == null)
			return "";

		return text.toLowerCase().replaceAll("[^0-9a-z]", "").trim();
	}

	public static boolean isLogicallyEquals(String string, String anotherString) {
		return cleanText(string).equalsIgnoreCase(cleanText(anotherString));
	}

	public static boolean isLogicallyContains(String string, String subString) {
		string = cleanText(string);
		subString = cleanText(subString);
		return string.contains(subString) || subString.contains(string);
	}

	public static Integer getInteger(String value) {
		if (value == null)
			value = "";
		else
			value = value.trim();
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static Long getLong(String value) {
		if (value == null)
			value = "";
		else
			value = value.trim();
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return 0L;
		}
	}

	public static Double getDouble(String value) {
		if (value == null)
			value = "";
		else
			value = value.trim();
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0D;
		}
	}

	private static BigDecimal getBigDecimal(String value) {
		if (value == null)
			value = "";
		else
			value = value.trim();
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

	public static String generate(String pattern) {
		if (pattern.trim().equalsIgnoreCase("auto"))
			pattern = "string";

		String group = null;
		String data = null;
		if (pattern.contains(">")) {
			int loc = pattern.indexOf(">");
			group = pattern.substring(0, loc).trim();
			data = pattern.substring(loc + 1).trim();
		} else {
			group = pattern;
		}

		switch (group.trim().toLowerCase()) {
		case "date":
		case "today":
		case "timestamp":
			return getDateTime(data);
		case "string":
		case "text":
			pattern = getString(pattern);
			break;
		case "number":
			break;
		}

		return pattern;
	}

	public static String getDateTime(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/YYYY");
		String expFormat = getDateFormat(pattern);
		if (expFormat != null)
			format = new SimpleDateFormat(expFormat);

		int offset = getNumeralPart(pattern);

		// Default offset manipulation for 'days' part

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, offset);
		String value = format.format(calendar.getTime());
		return value;
	}

	private static String getString(String pattern) {
		int count = getNumeralPart(pattern);
		if (count <= 0)
			count = 10;
		StringFormat format = getStringFormat(pattern);

		switch (format) {
		case ALPHA:
			return random(ALPHA, count);
		case ALPHA_NUMERIC:
			return random(ALPHA + NUMERIC, count);
		case ALPHA_NUMERIC_SPECIALS:
			return random(ALPHA + NUMERIC + SPECIALS, count);
		case ALPHA_SPECIALS:
			return random(ALPHA + SPECIALS, count);
		case NUMERIC:
			return random(NUMERIC, count);
		case NUMERIC_SPECIALS:
			return random(NUMERIC + SPECIALS, count);
		case SPECIALS:
			return random(SPECIALS, count);
		default:
			break;
		}
		return null;
	}

	private enum StringFormat {
		ALPHA, NUMERIC, SPECIALS, ALPHA_NUMERIC, ALPHA_SPECIALS, NUMERIC_SPECIALS, ALPHA_NUMERIC_SPECIALS
	}

	private static String getDateFormat(String pattern) {
		for (String patt : pattern.split(">")) {
			String tempPatt = patt.toUpperCase().trim();
			if (tempPatt.contains("|") || tempPatt.contains("/") || tempPatt.contains("\\") || tempPatt.contains("D")
					|| tempPatt.contains("M") || tempPatt.contains("Y") || tempPatt.contains("S")
					|| tempPatt.contains("H"))
				return patt;
		}
		return null;
	}
	

	private static StringFormat getStringFormat(String pattern) {
		for (String patt : pattern.split(">")) {
			switch (patt.trim().toLowerCase()) {
			case "a":
				return StringFormat.ALPHA;
			case "n":
				return StringFormat.NUMERIC;
			case "x":
				return StringFormat.SPECIALS;
			case "an":
			case "na":
				return StringFormat.ALPHA_NUMERIC;
			case "ax":
			case "xa":
				return StringFormat.ALPHA_SPECIALS;
			case "nx":
			case "xn":
				return StringFormat.NUMERIC_SPECIALS;
			case "anx":
			case "axn":
			case "xan":
			case "xna":
			case "nax":
			case "nxa":
				return StringFormat.ALPHA_NUMERIC_SPECIALS;
			}
		}
		return StringFormat.ALPHA;
	}

	private static int getNumeralPart(String pattern) {
		try {
			for (String patt : pattern.split(">")) {
				patt = patt.trim();
				if (NumberUtils.isCreatable(patt))
					return NumberUtils.createNumber(patt).intValue();
			}
		} catch (Exception e) {
		}
		return 0;
	}

	private static String random(final String charSet, final int length) {
		Random rd = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(charSet.charAt(rd.nextInt(charSet.length())));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String pattern = "today>3";
		System.out.println(generate(pattern));

		long test = getLong("90000000004");
		System.out.println(test);
	}
}
