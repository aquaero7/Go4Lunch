package com.example.go4lunch.utils;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static volatile Utils instance;
    private static Calendar calendar;
    private InputMethodManager imm;

    public Utils() {
        calendar = Calendar.getInstance();
    }

    public static Utils getInstance() {
        Utils result = instance;
        if (result != null) {
            return result;
        } else {
            instance = new Utils();
            return instance;
        }
    }

    /** For test use only : New instance factory **************************************************/
    public Utils(InputMethodManager imm) {
        calendar = Calendar.getInstance();
        this.imm = imm;
    }


    public static Utils getNewInstance(InputMethodManager imm) {
        instance = new Utils(imm);
        return instance;
    }
    /**********************************************************************************************/


    /****************
     * Device Utils *
     ****************/

    public void hideVirtualKeyboard(Context context, View view) {
        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /******************
     * Calendar utils *
     ******************/

    // Get current day of week
    public long getCurrentDayOfWeek() {
        // Returns a range from 1 (Monday) to 7 (Sunday)
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    // Get current formatted time
    public String getCurrentTime() {
        String hod = (calendar.get(Calendar.HOUR_OF_DAY) > 9) ?
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) : "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = (calendar.get(Calendar.MINUTE) > 9) ?
                String.valueOf(calendar.get(Calendar.MINUTE)) : "0" + String.valueOf(calendar.get(Calendar.MINUTE));

        return hod + min;
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

}
