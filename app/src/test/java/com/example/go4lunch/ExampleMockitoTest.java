package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;

import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Example local Mockito unit test, which will execute on the development machine (host).
 */
public class ExampleMockitoTest {

    MockitoSession mockito;

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
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();

        // Initialize the calendar objects
        initializeCalendar();
    }

    @After  // After each test
    public void tearDown() {
        // Stop Mockito strictness
        mockito.finishMocking();
    }


    @Test
    public void exampleSucceedTest() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void exampleFailedTest() {
        // assertEquals(4, 2 + 2);
        System.out.println("\nDamned !!!");
        System.err.println("Damned !!!");
        assertEquals("Wrong calculation", 4, 2 + 3);
    }

    @Test
    public void userMockitoTest() {
        //  // With mock
        User userMock = mock(User.class);
        userMock.setUsername("myName");
        System.out.println(userMock.getUsername());
        assertNull("Wrong name", userMock.getUsername());

        when(userMock.getUsername()).thenReturn("Toto");
        System.out.println(userMock.getUsername());
        assertEquals("Wrong name", "Toto", userMock.getUsername());
        //
        /*  With spy
        User userSpy = spy(User.class);
        userSpy.setUsername("myName");
        System.out.println(userSpy.getUsername());
        assertEquals("Wrong name", "myName", userSpy.getUsername());

        when(userSpy.getUsername()).thenReturn("Toto");
        System.out.println(userSpy.getUsername());
        assertEquals("Wrong name", "Toto", userSpy.getUsername());
        */
    }

    @Test
    public void calendarUtilsTest() {
        System.out.println(currentDate);
        System.out.println(CalendarUtils.getCurrentDate());

        assertEquals("Wrong date", currentDate, CalendarUtils.getCurrentDate());
    }

    @Test
    public void calendarUtilsMockitoTest() {
        System.out.println(CalendarUtils.getCurrentDate());
        assertEquals("Wrong date", currentDate, CalendarUtils.getCurrentDate());

        /** Use a try with resources to limit the scope of the static method mocking to its test method.
         * So we only mock the static class in that test method */
        try (MockedStatic<CalendarUtils> utilities = Mockito.mockStatic(CalendarUtils.class)) {
            System.out.println(CalendarUtils.getCurrentDate());
            assertNull("Wrong date", CalendarUtils.getCurrentDate());
            utilities.when(CalendarUtils::getCurrentDate).thenReturn("myDate");
            System.out.println(CalendarUtils.getCurrentDate());
            assertEquals("Wrong date", "myDate", CalendarUtils.getCurrentDate());
        }

        System.out.println(CalendarUtils.getCurrentDate());
        assertEquals("Wrong date", currentDate, CalendarUtils.getCurrentDate());
    }

    @Test
    public void calendarUtilsMockitoTest2() {
        // assertEquals("Wrong date", "myDate", CalendarUtils.getCurrentDate());
        // assertNull("Wrong date", CalendarUtils.getCurrentDate());
        assertEquals("Wrong date", currentDate, CalendarUtils.getCurrentDate());

        MockedStatic<CalendarUtils> utilities = Mockito.mockStatic(CalendarUtils.class);
        assertNull("Wrong date", CalendarUtils.getCurrentDate());
        utilities.when(CalendarUtils::getCurrentDate).thenReturn("myDate");
        // calendarUtilsMock.when(CalendarUtils::getCurrentDate).thenReturn("myDate");

        assertEquals("Wrong date", "myDate", CalendarUtils.getCurrentDate());
    }

    @Test
    public void instantMockitoTest() {
        Instant result = Instant.now();
        long sec = result.getEpochSecond();
        System.out.println(sec);
        assertNotNull("Wrong instant", Instant.now());
        assertEquals("Wrong second", sec, result.getEpochSecond());


        try (MockedStatic<Instant> instantStaticMock = mockStatic(Instant.class)) {
                                                                            // Create static mock for java.time.Instant
            assertNull("Wrong instant", Instant.now());

            Instant instantMock = mock(Instant.class);                      // Create mock for Instant, hashCode: ..........

            when(instantMock.getEpochSecond()).thenReturn(0L);        // Stub method call
            instantStaticMock.when(Instant::now).thenReturn(instantMock);   // Stub static method call

            assertNotNull("Wrong instant", Instant.now());
            assertEquals("Wrong second", 0, Instant.now().getEpochSecond());    // Verify
        }
    }

    @Test
    public void instantMockitoTestPersonal() {

        Instant initInstant = Instant.now();
        long initEpoSec = initInstant.getEpochSecond();

        try (MockedStatic<Instant> instantStaticMock = mockStatic(Instant.class)) {

            instantStaticMock.when(Instant::now).thenReturn(initInstant);

            Instant newInstant = Instant.now();
            long newEpoSec = newInstant.getEpochSecond();

            assertEquals("Wrong instant", initInstant, newInstant);
            assertEquals("Wrong second", initEpoSec, newEpoSec);

        }
    }

    @Test
    public void localDateTimeMockitoTest() {

        try (MockedStatic<LocalDateTime> ldtStaticMock = mockStatic(LocalDateTime.class)) {
                                                                            // Create static mock for java.time.LocalDateTime
            LocalDateTime ldtMock = mock(LocalDateTime.class);              // Create mock for LocalDateTime, hashCode: ..........

            when(ldtMock.getMinute()).thenReturn(30);                 // Stub method call
            ldtStaticMock.when(LocalDateTime::now).thenReturn(ldtMock);     // Stub static method call

            assertEquals("Wrong minute", 30, LocalDateTime.now().getMinute());  // Verify

        }
    }

    @Test
    public void verificationTest() {
        // Test on non static method
        User mock = mock(User.class);
        when(mock.getUsername()).thenReturn("Toto");

        mock.getUsername();                                          // Method called once
        String nameResult = mock.getUsername();                      // Method called twice

        System.out.println(nameResult);
        verify(mock, times(2)).getUsername();
        assertEquals("Wrond name", "Toto", mock.getUsername());

        // Test on static method
        try (MockedStatic<CalendarUtils> staticMock = mockStatic(CalendarUtils.class)) {
            staticMock.when(CalendarUtils::getCurrentDate).thenReturn("20230101");

            CalendarUtils.getCurrentDate();                         // Method called once
            String dateResult =  CalendarUtils.getCurrentDate();    // Method called twice

            System.out.println(dateResult);
            staticMock.verify(CalendarUtils::getCurrentDate, times(2));
            assertEquals("Wrond date", "20230101", CalendarUtils.getCurrentDate());
        }
        //
    }





}
