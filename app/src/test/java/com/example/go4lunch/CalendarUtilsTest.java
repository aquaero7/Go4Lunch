package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import com.example.go4lunch.utils.CalendarUtils;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtilsTest {

    String currentDate;
    String currentTime;
    long currentDayOfWeek;


    private void initializeCalendar() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        Date date = calendar.getTime();
        currentDate = sdf.format(date);

        String hod = (calendar.get(Calendar.HOUR_OF_DAY) > 9) ?
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) : "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = (calendar.get(Calendar.MINUTE) > 9) ?
                String.valueOf(calendar.get(Calendar.MINUTE)) : "0" + String.valueOf(calendar.get(Calendar.MINUTE));
        currentTime = hod + min;

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }


    @Before // Before each test
    public void setup() {
        // Initialize the calendar objects
        initializeCalendar();
    }


    @Test
    public void getCurrentDayOfWeekWithSuccess() {
        assertEquals("Wrong day of week", currentDayOfWeek, CalendarUtils.getCurrentDayOfWeek());
    }

    @Test
    public void getCurrentTimeWithSuccess() {
        assertEquals("Wrong time", currentTime, CalendarUtils.getCurrentTime());
    }

    @Test
    public void getCurrentDateWithSuccess() {
        assertEquals("Wrong date", currentDate, CalendarUtils.getCurrentDate());
    }

}
