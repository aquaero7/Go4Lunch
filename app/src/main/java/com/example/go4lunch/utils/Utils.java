package com.example.go4lunch.utils;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.api.model.Period;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static volatile Utils instance;
    private static final Calendar calendar = Calendar.getInstance();

    public static Utils getInstance() {
        Utils result = instance;
        if (result != null) {
            return result;
        } else {
            instance = new Utils();
            return instance;
        }
    }

    /*********
     * Utils *
     *********/

    public static void hideVirtualKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /******************
     * Calendar utils *
     ******************/

    // Get current day of week
    public static long getCurrentDayOfWeek() {
        // Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    // Get current formatted time
    public static String getCurrentTime() {
        // Calendar calendar = Calendar.getInstance();

        String hod = (calendar.get(Calendar.HOUR_OF_DAY) > 9) ?
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) : "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = (calendar.get(Calendar.MINUTE) > 9) ?
                String.valueOf(calendar.get(Calendar.MINUTE)) : "0" + String.valueOf(calendar.get(Calendar.MINUTE));

        return hod + min;
    }

    public static String getCurrentDate() {
        // Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

}
