package com.example.go4lunch.utils;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.Photo;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreUtils {

    // Get restaurant data from firestore with given document and create restaurant
    public static Restaurant getRestaurantFromDatabaseDocument(QueryDocumentSnapshot document, String KEY) {
        String rId = Objects.requireNonNull(document.getData().get("id")).toString();
        String rName = Objects.requireNonNull(document.getData().get("name")).toString();
        long rDistance = (long) document.getData().get("distance");

        List<Photo> rPhoto = (List<Photo>) document.getData().get("photos");             // TODO : To analyse and complete for display
        Map<Photo, Object> myPhoto = (Map<Photo, Object>) rPhoto.get(0);
        String photoRef = myPhoto.get("photoReference").toString();
        String photoUrl = Photo.getPhotoUrl(photoRef, KEY);
        // List<Photo> rPhoto = null;

        String rNationality = Objects.requireNonNull(document.getData().get("nationality")).toString();
        String rAddress = Objects.requireNonNull(document.getData().get("address")).toString();
        double rRating = (double) document.getData().get("rating");
        String rOpeningInformation = getOpeningInformation(document);
        int rLikesCount = Integer.parseInt(Objects.requireNonNull(document.getData().get("likesCount")).toString());
        String rPhoneNumber = document.getData().get("phoneNumber") != null ? document.getData().get("phoneNumber").toString() : "";
        String rWebsite = document.getData().get("website") != null ?  document.getData().get("website").toString() : "";
        // Geometry rGeometry = (Geometry) document.getData().get("geometry");
        Geometry rGeometry = null;
        // List<User> rSelectors = (List<User>) document.getData().get("selectors");
        List<User> rSelectors = null;

        Restaurant restaurantFromData = new Restaurant(rId, rName, rDistance, rPhoto, rNationality, rAddress,
                rRating, rOpeningInformation, rLikesCount, rPhoneNumber, rWebsite, rGeometry, rSelectors);

        return restaurantFromData;
    }

    public static String getOpeningInformation(QueryDocumentSnapshot document) {

        String openingInformation = "";
        String closingTime1 = "";
        String openingTime1 = "";
        String closingTime2 = "";
        String openingTime2 = "";

        boolean openNow = (boolean) ((Map<String, Object>) document.getData().get("openingHours")).get("openNow");

        // TODO : To be replaced by method below
        openingInformation = openNow ? "open" : "closed";
        //

        // TODO : Replacement getting and displaying more info from API
        long currentDayOfWeek = CalendarUtils.getCurrentDayOfWeek();
        String currentTime = CalendarUtils.getCurrentTime();

        ArrayList periodsList = (ArrayList) ((Map<String, Object>) document.getData().get("openingHours")).get("periods");

        /*
        int nbOfPeriods = periodsList.size();

        Map<String, Object> period0 = (Map<String, Object>) periodsList.get(0);
        Map<String, Object> period0ClosingInfo = (Map<String, Object>) period0.get("close");
        String period0ClosingDay = period0ClosingInfo.get("day").toString();
        String period0ClosingTime = period0ClosingInfo.get("time").toString();
        Map<String, Object> period0OpeningInfo = (Map<String, Object>) period0.get("open");
        String period0OpeningDay = period0OpeningInfo.get("day").toString();
        String period0OpeningTime = period0OpeningInfo.get("time").toString();

        String p0ClosingDay = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(0)).get("close")).get("day").toString();
        String p0ClosingTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(0)).get("close")).get("time").toString();
        String p0OpeningDay = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(0)).get("open")).get("day").toString();
        String p0OpeningTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(0)).get("open")).get("time").toString();
        */

        for (int i = 0 ; i < periodsList.size() ; i++) {
            long pClosingDay = (long) ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("close")).get("day");
            String pClosingTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("close")).get("time").toString();
            long pOpeningDay = (long) ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("open")).get("day");
            String pOpeningTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("open")).get("time").toString();

            if (pClosingDay == currentDayOfWeek && pOpeningDay == currentDayOfWeek) {
                if ((closingTime1.isEmpty()) && (openingTime1.isEmpty())) {
                    closingTime1 = pClosingTime;
                    openingTime1 = pOpeningTime;
                } else {
                    closingTime2 = pClosingTime;
                    openingTime2 = pOpeningTime;

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

        if (openNow) {
            if (closingTime1.equals("0000") || closingTime2.equals("0000"))  {
                openingInformation = "open";
            }else if (currentTime.compareTo(closingTime1) < 0) {
                openingInformation = "Open until " + closingTime1;
            } else {
                openingInformation = "Open until " + closingTime2;
            }
        } else {
            if(!closingTime1.isEmpty() || !closingTime2.isEmpty()) {
                if (currentTime.compareTo(openingTime1) < 0) {
                    openingInformation = "Open at " + openingTime1;
                } else if (currentTime.compareTo(openingTime2) < 0) {
                    openingInformation = "Open at " + openingTime2;
                } else {
                    openingInformation = "closed";
                }
            }
        }

        return openingInformation;
    }


}
