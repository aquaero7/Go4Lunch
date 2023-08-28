package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.go4lunch.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilsTest {
    private MockitoSession mockito;
    Context contextMock;
    View viewMock;
    InputMethodManager immMock;
    private String currentDate;
    private String currentTime;
    private long currentDayOfWeek;
    private Utils utils;

    private void initializeData() {
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();

        contextMock = mock(Context.class);
        viewMock = mock(View.class);
        immMock = mock(InputMethodManager.class);

        // Class under test
        utils = Utils.getNewInstance(immMock);
    }

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

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }


    @Before // Before each test
    public void setup() {
        initializeData();
        initializeCalendar();
    }

    @After  // After each test
    public void tearDown() {
        // Stop Mockito strictness
        mockito.finishMocking();
    }


    /***************
     * Device test *
     ***************/

    @Test
    public void hideVirtualKeyboardWithSuccess() {
        when(contextMock.getSystemService(anyString())).thenReturn(immMock);

        utils.hideVirtualKeyboard(contextMock, viewMock);

        verify(contextMock, times(1)).getSystemService(anyString());
        verify(immMock, times(1)).hideSoftInputFromWindow(viewMock.getWindowToken(), 0);
    }


    /******************
     * Calendar tests *
     ******************/

    @Test
    public void getCurrentDayOfWeekWithSuccess() {
        assertEquals("Wrong day of week", currentDayOfWeek, utils.getCurrentDayOfWeek());
    }

    @Test
    public void getCurrentTimeWithSuccess() {
        assertEquals("Wrong time", currentTime, utils.getCurrentTime());
    }

    @Test
    public void getCurrentDateWithSuccess() {
        assertEquals("Wrong date", currentDate, utils.getCurrentDate());
    }

}
