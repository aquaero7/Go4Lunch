package com.example.go4lunch;

import com.example.go4lunch.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

public class SettingsViewModelTest {
    MockitoSession mockito;
    String currentDate;
    String currentTime;
    long currentDayOfWeek;
    Utils utils;

    private void initializeData() {

    }

    private void initializeCalendar() {

    }

    @Before // Before each test
    public void setup() {
        utils = Utils.getInstance();
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();

        initializeData();
        initializeCalendar();
    }

    @After  // After each test
    public void tearDown() {
        // Stop Mockito strictness
        mockito.finishMocking();
    }


    /*********
     * Tests *
     *********/

    @Test
    public void testExample() {


    }





}
