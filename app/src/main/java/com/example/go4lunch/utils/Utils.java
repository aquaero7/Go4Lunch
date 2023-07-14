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

    public static int calculateRestaurantDistance(Restaurant restaurant, LatLng currentLatLng) {
        double restaurantLat = restaurant.getGeometry().getLocation().getLat();
        double restaurantLng = restaurant.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
        double distance = SphericalUtil.computeDistanceBetween(currentLatLng, restaurantLatLng);

        return (int) distance;  /** Distance in meters */
    }

    public static List<RestaurantWithDistance> updateRestaurantsListWithDistances(List<Restaurant> restaurantsList, LatLng home) {
        List<RestaurantWithDistance> restaurants = new ArrayList();
        for (Restaurant restaurant : restaurantsList) {
            int distance = calculateRestaurantDistance(restaurant, home);

            RestaurantWithDistance restaurantWithDistance
                    = new RestaurantWithDistance(restaurant.getRid(), restaurant.getName(),
                    restaurant.getPhotos(), restaurant.getAddress(), restaurant.getRating(),
                    restaurant.getOpeningHours(), restaurant.getPhoneNumber(),
                    restaurant.getWebsite(), restaurant.getGeometry(), distance);

            restaurants.add(restaurantWithDistance);
        }
        return restaurants;
    }

    public static LatLngBounds calculateBounds(LatLng home, int radius) {
        /** Distances in meters / Headings in degrees */
        double distanceToCorner = radius * Math.sqrt(2);
        LatLng sw = SphericalUtil.computeOffset(home, distanceToCorner, 225);   // 5*PI/4
        LatLng ne = SphericalUtil.computeOffset(home, distanceToCorner, 45);    // PI/4

        return new LatLngBounds(sw, ne);
    }

    public static void sortByDistanceAndName (List<RestaurantWithDistance> restaurantsWithDistance) {
        Collections.sort(restaurantsWithDistance, RestaurantWithDistance.comparatorName);
        Collections.sort(restaurantsWithDistance, RestaurantWithDistance.comparatorDistance);
    }

    public static void sortByName(List<User> workmatesList) {
        Collections.sort(workmatesList, User.comparatorName);
    }

    public static void sortByAscendingOpeningTime(List<Period> periods) {
        // Using Comparator
        periods.sort(Comparator.comparing(o -> o.getOpen().getTime()));
        // periods.sort(Comparator.comparing(o -> o.getOpen().getTime(), Comparator.naturalOrder()));

        // Or without using Comparator
        // periods.sort((o1, o2) -> o1.getOpen().getTime().compareTo(o2.getOpen().getTime()));
    }

    public static void sortByDescendingOpeningTime(List<Period> periods) {
        // Using Comparator
        periods.sort(Comparator.comparing(o -> o.getOpen().getTime(), Comparator.reverseOrder()));

        // Or without using Comparator
        // periods.sort((o1, o2) -> o2.getOpen().getTime().compareTo(o1.getOpen().getTime()));
    }

    public static void hideVirtualKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /******************
     * Calendar utils *
     ******************/

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

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        Date date = calendar.getTime();
        return sdf.format(date);
    }
}
