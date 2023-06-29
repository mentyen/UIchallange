package com.cucumber.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


public class USHolidays {

    public static void main(String[] args) {
        List<LocalDate> holidays = USHolidays.getHolidays(2023);
        for (LocalDate holiday : holidays) {
            System.out.println(holiday);
        }
    }

    public static List<LocalDate> getHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();
        // New Year's Day
        holidays.add(LocalDate.of(year, Month.JANUARY, 1));
        // Martin Luther King Jr. Day (third Monday in January)
        holidays.add(getNthWeekdayOfMonth(year, Month.JANUARY, 3, DayOfWeek.MONDAY));
        // Presidents' Day (third Monday in February)
        holidays.add(getNthWeekdayOfMonth(year, Month.FEBRUARY, 3, DayOfWeek.MONDAY));
        // Memorial Day (last Monday in May)
        holidays.add(getLastWeekdayOfMonth(year, Month.MAY, DayOfWeek.MONDAY));
        // Independence Day
        holidays.add(LocalDate.of(year, Month.JULY, 4));
        // Labor Day (first Monday in September)
        holidays.add(getNthWeekdayOfMonth(year, Month.SEPTEMBER, 1, DayOfWeek.MONDAY));
        // Columbus Day (second Monday in October)
        holidays.add(getNthWeekdayOfMonth(year, Month.OCTOBER, 2, DayOfWeek.MONDAY));
        // Veterans Day
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 11));
        // Thanksgiving Day (fourth Thursday in November)
        holidays.add(getNthWeekdayOfMonth(year, Month.NOVEMBER, 4, DayOfWeek.THURSDAY));
        // Christmas Day
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25));
        return holidays;
    }

    private static LocalDate getNthWeekdayOfMonth(int year, Month month, int n, DayOfWeek dayOfWeek) {
        LocalDate date = LocalDate.of(year, month, 1);
        int count = 0;
        while (date.getDayOfWeek() != dayOfWeek || ++count != n) {
            date = date.plusDays(1);
        }
        return date;
    }

    private static LocalDate getLastWeekdayOfMonth(int year, Month month, DayOfWeek dayOfWeek) {
        LocalDate date = LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
        while (date.getDayOfWeek() != dayOfWeek) {
            date = date.minusDays(1);
        }
        return date;
    }


}
