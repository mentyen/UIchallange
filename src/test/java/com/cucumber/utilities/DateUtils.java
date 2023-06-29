package com.cucumber.utilities;

import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

	@Test
	public void testDate() throws ParseException {
		DateUtils obj=new DateUtils();

		String str_date = "13-Jan-2023 19:52:29";

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		String st1= sdf.format(sdf.parse("13-Jan-2023 19:52:29")).toString();
		String st2=formatDate(str_date,"dd-MMM-yyyy");
		System.out.println(st1);
		System.out.println(st2);
	}

	/**
	 * This method will return you a day off the week in String format. MONDAY
	 */
	public static String getCurrentDayOfTheWeek() {
		Instant instant = Instant.now();
		ZonedDateTime zdt = instant.atZone(ZoneId.of("America/New_York"));
		return zdt.getDayOfWeek().toString();
	}

	public static String convertofEpochMilli(int date) {		
		String value = String.valueOf(date * 24 * 60 * 60) + "000";
		long epochSecond = Long.parseLong(value, 10);
		Instant instant = Instant.ofEpochMilli(epochSecond);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return String.valueOf(fmt.format(instant.atZone(ZoneId.of("UTC"))));
	}

	/**
	 * This method parses the Date object into specific String format.
	 * 
	 * @param date
	 * @param format
	 * @return String
	 */
	public static String parseIntoString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * This method parses the String object of specific format to Date object.
	 * 
	 * @param date
	 * @param format
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parseIntoDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method parses the String object of specific format to Date object.
	 * 
	 * @param date
	 * @param format
	 * @return String
	 * @throws Exception
	 */
	public static String formatDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.format(sdf.parse(date)).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method checks if input String is a valid Date.
	 * 
	 * @param date
	 * @param format
	 * @return Date
	 * @throws ParseException
	 */
	public static boolean isDate(String date, String format) {
		Date dateVal = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			dateVal = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateVal != null;
	}

	/**
	 * This method will return the current date in the specific format.
	 *
	 * @param format
	 * @return String
	 */
	public static String getCurrentDate(String format) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return parseIntoString(date, format);
	}

	/**
	 * This method will return tommorrow's date in String format.
	 *
	 * @param format
	 * @return String
	 */
	public static String getTomorrowDate(String format) {
		return parseIntoString(getDateWrtCurrentDate(1), format);
	}

	/**
	 * This method will return yesterday's date in String format.
	 * 
	 * @param format
	 * @return String
	 */
	public static String getYesterdayDate(String format) {
		return parseIntoString(getDateWrtCurrentDate(-1), format);
	}	
	

	/**
	 * This method will return yesterday's date in String format.
	 * 
	 * @param format
	 * @return String
	 */
	public static String getMondaysDateOfCurrentWeek(String format) {
		LocalDate previousMonday = LocalDate.now(ZoneId.of("America/Montreal"))
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return formatter.format(previousMonday);
	}

	/**
	 * This method will return future's date in String format.
	 * 
	 * @param format
	 * @return String
	 */
	public static String getFutureDate(String format) {
		int diff = (int) (Math.random() * 999);
		return parseIntoString(getDateWrtCurrentDate(diff), format);
	}

	/**
	 * This method will return past date in String format.
	 * 
	 * @param format
	 * @return String
	 */
	public static String getPastDate(String format) {
		int diff = (int) (Math.random() * 999);
		return parseIntoString(getDateWrtCurrentDate(-diff), format);
	}

	private static Date getDateWrtCurrentDate(int diff) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, diff);
		return cal.getTime();
	}

	/**
	 * This method will return UTC date in String format.
	 * 
	 * @param String
	 * @return String
	 */
	private static String convertDateToUTC(String dateToConvert, String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(dateToConvert);
		Calendar currentdate = Calendar.getInstance();
		currentdate.setTime(date);
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		TimeZone obj = TimeZone.getTimeZone("UTC");
		formatter.setTimeZone(obj);
		return formatter.format(currentdate.getTime()).toString();
	}

	/**
	 * This method will return UTC date in String format.
	 * 
	 * @param String
	 * @return String
	 */
	public static String getCurrentTime() {
		String formattedDate = null;
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			formattedDate = sdf.format(date);
		} catch (Exception e) {
			System.out.println("Fail to get current time, exception accured:-->" + e.getMessage());
		}
		return formattedDate;
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	public static String formatDate(Date date,String format){
		try{
			SimpleDateFormat sm = new SimpleDateFormat(format);
			return sm.format(date);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
	

}
