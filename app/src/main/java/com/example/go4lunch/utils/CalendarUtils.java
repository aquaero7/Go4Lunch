package com.example.go4lunch.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    // Get current day of week
    public static long getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    // Get current formatted time
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();

        String hod = (calendar.get(Calendar.HOUR_OF_DAY) > 9) ?
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) : "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = (calendar.get(Calendar.MINUTE) > 9) ?
                String.valueOf(calendar.get(Calendar.MINUTE)) : "0" + String.valueOf(calendar.get(Calendar.MINUTE));

        return hod + min;
    }

}
