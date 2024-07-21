package com.example.financialmanagerapp.utils;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.util.Calendar;

public class TimerFormatter {
    @SuppressLint("DefaultLocale")
    public static String convertDateString(int year, int month, int day) {
        if (month < 1) month = 1;
        if (month > 12) month = 12;

        if (day < 1) day = 1;
        if (day > 31) day = 31;

        String dayStr = formatNumber(day);
        String monthStr = formatNumber(month);
        return monthStr + "/" + dayStr + "/" + year;
    }

    public static String convertTimeString(int hour, int minute) {
        if (hour >= 24) hour = 0;
        if (hour < 0) hour = 0;

        if (minute < 0) minute = 0;
        if (minute >= 60) minute = 0;

        String hourStr = formatNumber(hour);
        String minuteStr = formatNumber(minute);

        return hourStr + ":" + minuteStr;
    }

    @SuppressLint("DefaultLocale")
    private static String formatNumber(int num) {

        return String.format("%02d", num);
    }

    public static Calendar getCalendar(Timestamp timestamp) {

        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the time of the calendar to the timestamp
        calendar.setTimeInMillis(timestamp.getTime());

        // can use to get year, get day, get day of week, etc
        return calendar;

    }
}
