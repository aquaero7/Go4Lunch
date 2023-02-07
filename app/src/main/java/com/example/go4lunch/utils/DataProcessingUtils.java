package com.example.go4lunch.utils;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProcessingUtils {

    public static int calculateRestaurantDistance(Restaurant restaurant, LatLng currentLatLng) {
        double restaurantLat = restaurant.getGeometry().getLocation().getLat();
        double restaurantLng = restaurant.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
        // Distance in meters
        double distance = SphericalUtil.computeDistanceBetween(currentLatLng, restaurantLatLng);

        return (int) distance;
    }

    public static List<Restaurant> customizeRestaurantsList(List<Restaurant> restaurantsList) {
        List<Restaurant> customizedRestaurantsList = new ArrayList<>();
        for (Restaurant restaurant : restaurantsList) {
            String rId = restaurant.getId();
            String rName = restaurant.getName();
            long rDistance = restaurant.getDistance();
            List<Photo> rPhotos = restaurant.getPhotos();
            String rNationality = "Frenchy";   // TODO
            String rAddress = restaurant.getAddress();
            double rRating = restaurant.getRating();
            String rOpeningInformation = getOpeningInformation(restaurant);
            int rLikesCount = 0;    // TODO
            String rPhoneNumber = restaurant.getPhoneNumber();
            String rWebsite = restaurant.getWebsite();
            Geometry rGeometry = restaurant.getGeometry();
            List<User> rSelectors = null;   // TODO

            customizedRestaurantsList.add(new Restaurant(rId, rName, rDistance, rPhotos, rNationality, rAddress,
                    rRating, rOpeningInformation, rLikesCount, rPhoneNumber, rWebsite, rGeometry, rSelectors));
        }

        sortByDistanceAndName(customizedRestaurantsList);
        return customizedRestaurantsList;
    }

    public static String getOpeningInformation(Restaurant restaurant) {
        String openingInformation = "";
        if (restaurant.getOpeningHours() != null) {
            // Possibility of 2 opening and closing periods in a day
            String closingTime1 = "";
            String openingTime1 = "";
            String closingTime2 = "";
            String openingTime2 = "";

            boolean openNow = restaurant.getOpeningHours().isOpenNow();

            // TODO : To be deleted and replaced by method below
            openingInformation = openNow ? "open" : "closed";
            //

            // TODO : Replacement getting and displaying more info from API
            long currentDayOfWeek = CalendarUtils.getCurrentDayOfWeek();
            String currentTime = CalendarUtils.getCurrentTime();

            // Get the list of opening periods
            List<OpeningHours.Period> periodsList = restaurant.getOpeningHours().getPeriods();

            // Get details for each period p
            for (int i = 0; i < periodsList.size(); i++) {
                long pClosingDay = periodsList.get(i).getClose().getDay();
                String pClosingTime = periodsList.get(i).getClose().getTime();
                long pOpeningDay = periodsList.get(i).getOpen().getDay();
                String pOpeningTime = periodsList.get(i).getOpen().getTime();

                // If period day matches with current day
                if (pClosingDay == currentDayOfWeek && pOpeningDay == currentDayOfWeek) {
                    // Get information for period 1 first...
                    if ((closingTime1.isEmpty()) && (openingTime1.isEmpty())) {
                        closingTime1 = pClosingTime;
                        openingTime1 = pOpeningTime;
                        // ...then for period 2
                    } else {
                        closingTime2 = pClosingTime;
                        openingTime2 = pOpeningTime;

                        // Sort periods in ascending chronological order
                        if (closingTime1.compareTo(closingTime2) > 0) {
                            String cTmp = closingTime1;
                            closingTime1 = closingTime2;
                            closingTime2 = cTmp;
                        }
                        if (openingTime1.compareTo(openingTime2) > 0) {
                            String oTmp = openingTime1;
                            openingTime1 = openingTime2;
                            openingTime2 = oTmp;
                        }
                        break;
                    }
                }
            }

            /*  Define information to display
                Information must be either 3 char (code) or 7 char (code+schedule) length   */
            if (openNow) {
                if (closingTime1.equals("0000") || closingTime2.equals("0000")) {
                    openingInformation = "OP*";                     // Open 24/7
                } else if (currentTime.compareTo(closingTime1) < 0) {
                    openingInformation = "OPU" + closingTime1;      // Open until...
                } else {
                    openingInformation = "OPU" + closingTime2;      // Open until...
                }
            } else {
                if (!closingTime1.isEmpty() || !closingTime2.isEmpty()) {
                    if (currentTime.compareTo(openingTime1) < 0) {
                        openingInformation = "OPA" + openingTime1;  // Open at
                    } else if (currentTime.compareTo(openingTime2) < 0) {
                        openingInformation = "OPA" + openingTime2;  // Open at
                    } else {
                        openingInformation = "CLO";                 // Closed
                    }
                }
            }
        }
        return openingInformation;
    }

    public static void sortByDistanceAndName (List<Restaurant> restaurantList) {
        Collections.sort(restaurantList, Restaurant.comparatorName);
        Collections.sort(restaurantList, Restaurant.comparatorDistance);
    }


}
