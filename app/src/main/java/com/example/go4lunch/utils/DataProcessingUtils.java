package com.example.go4lunch.utils;


import android.util.Log;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Period;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataProcessingUtils {

    public static int calculateRestaurantDistance(Restaurant restaurant, LatLng currentLatLng) {
        double restaurantLat = restaurant.getGeometry().getLocation().getLat();
        double restaurantLng = restaurant.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
        double distance = SphericalUtil.computeDistanceBetween(currentLatLng, restaurantLatLng);

        return (int) distance;  /** Distance in meters */
    }

    public static String getOpeningInformation(RestaurantWithDistance restaurant) {
        // Built information to display

        String openingInformation = "";
        if (restaurant.getOpeningHours() != null) {
            // Possibility of several opening and closing periods in a day
            /** Information must be either 3 char (code) or 7 char (code+schedule) length */

            boolean openNow;
            long currentDayOfWeek = CalendarUtils.getCurrentDayOfWeek();
            String currentTime = CalendarUtils.getCurrentTime();

            // Get the list of opening periods
            List<Period> periods = restaurant.getOpeningHours().getPeriods();

            if (periods != null) {
                // There is at least one period
                List<Period> todayPeriods = new ArrayList<>();

                if (periods.size() == 1
                        && periods.get(0).getClose() == null
                        && periods.get(0).getOpen().getTime().equals("0000")) {
                    // If there is only one period, and it is open all week
                    openingInformation = "OP*";                                         // Open 24/7

                } else {
                    /* There is at least one period, so, we get details for each period...
                       ...and we get today's periods */
                    for (Period period : periods) {
                        // If period day matches with current day, add period to today periods list
                        if (period.getOpen().getDay() == currentDayOfWeek) todayPeriods.add(period);
                    }

                    // Analyze today's periods

                    if (todayPeriods.size() == 0) {
                        // There is no period for today, so it is closed
                        openingInformation = "CLO";                                        // Closed

                    } else if (todayPeriods.size() == 1
                            && todayPeriods.get(0).getOpen().getTime().equals("0000")
                            && todayPeriods.get(0).getClose() != null
                            && todayPeriods.get(0).getClose().getTime().equals("0000")
                            && todayPeriods.get(0).getClose().getDay() == currentDayOfWeek + 1) {
                        // If there is only one period for today, so it is open all day
                        openingInformation = "OPD";                                // Open H24 today

                    } else {
                        // If there is at least one period for today : ...

                        // Sort today periods list by ascending opening time
                        sortByAscendingOpeningTime(todayPeriods);

                        // Calculate if the restaurant is currently open or closed
                        Period todayLastPeriod = todayPeriods.get(todayPeriods.size()-1);
                        openNow = false;

                        for (Period period : todayPeriods) {
                            if ((currentTime.compareTo(period.getOpen().getTime()) >= 0
                                    && currentTime.compareTo(period.getClose().getTime()) <= 0)) {
                                openNow = true;
                            }
                        }
                        if (currentTime.compareTo(todayLastPeriod.getOpen().getTime()) >= 0
                                && todayLastPeriod.getClose().getDay() != todayLastPeriod.getOpen().getDay()) {
                            openNow = true;
                        }

                        /* Determine opening information according to
                        whether the restaurant is currently open or closed */
                        if (openNow) {
                            // It is currently open
                            for (Period period : todayPeriods) {
                                String schedule = period.getClose().getTime();
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Closing today at...
                                    openingInformation = "OPU" + schedule;          // Open until...
                                    break;
                                }
                            }
                            /* Current time doesn't match periods above...
                               ...so we check if last period ends after midnight */
                            if (openingInformation.isEmpty()) {
                                if (todayLastPeriod.getClose().getDay() != todayLastPeriod.getOpen().getDay()) {
                                    // Closing after midnight (last period schedule)
                                    String schedule = todayLastPeriod.getClose().getTime();
                                    openingInformation = "OPU" + schedule;          // Open until...
                                } else {
                                    // Unexpected case... A problem occurs somewhere !
                                    openingInformation = "???";             // Unknown opening hours
                                    Log.w("DataProcessingUtils",
                                            "A problem has occurred when trying to retrieve opening information");
                                }
                            }

                        } else {
                            // It is currently closed
                            for (Period period : todayPeriods) {
                                String schedule = period.getOpen().getTime();
                                if (currentTime.compareTo(schedule) < 0 ) {
                                    // Opening today at...
                                    openingInformation = "OPA" + schedule;             // Open at...
                                    break;
                                }
                            }
                            if (openingInformation.isEmpty()) {
                                openingInformation = "CLO";                                // Closed
                            }
                        }
                    }
                }

            } else {
                // No information about opening hours (periods is null)
                openingInformation = "???";                                 // Unknown opening hours
            }

        } else {
            // No information about opening hours (openingHours is null)
            openingInformation = "???";                                     // Unknown opening hours
        }

        return openingInformation;
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

    /*  // TODO : To be deleted
    public static LatLngBounds calculateBoundsOld(LatLng home, int radius) {
        //** Distances in meters / Angles in radians //
        double earthRadius = 6371e3;
        double earthCircle = 2 * Math.PI * earthRadius;
        double latHome = home.latitude;
        double lngHome = home.longitude;
        double latDegreeInMeters = 1 * Math.PI * earthRadius / 180;
        double lngDegreeInMeters = 2 * Math.PI * earthRadius / 360;
        double angle = Math.PI / 4;
        double curveCoef = (earthCircle / 2 * angle) / (2 * earthRadius * Math.sin(angle / 2)) / Math.PI;
        double dLat = radius * curveCoef / (latDegreeInMeters * Math.sin(lngHome));
        double dLng = radius * curveCoef / (lngDegreeInMeters * Math.sin(latHome));

        double latNE = latHome + dLat;
        double lngNE = lngHome - dLng;
        double latSW = latHome - dLat;
        double lngSW = lngHome + dLng;
        LatLng sw = new LatLng(latSW, lngSW);
        LatLng ne = new LatLng(latNE, lngNE);

        return new LatLngBounds(sw, ne);
    }
    */

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

}
