package com.example.go4lunch.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.Location;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreUtils {

    // Get user data from firestore with given document and create user
    public static User getUserFromDatabaseDocument(QueryDocumentSnapshot document) {
        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
        String uName = Objects.requireNonNull(document.getData().get("username")).toString();
        String uEmail = ((document.getData().get("userEmail")) != null) ? document.getData().get("userEmail").toString() : null;
        String uUrlPicture = ((document.getData().get("userUrlPicture")) != null) ? document.getData().get("userUrlPicture").toString() : null;
        String selectedRestaurantId = ((document.getData().get("selectedRestaurantId")) != null) ? document.getData().get("selectedRestaurantId").toString() : null;
        String selectedRestaurantName = ((document.getData().get("selectedRestaurantName")) != null) ? document.getData().get("selectedRestaurantName").toString() : null;
        String selectionDate = ((document.getData().get("selectionDate")) != null) ? document.getData().get("selectionDate").toString() : null;
        List<String> likedRestaurantsIds = null;

        User userFromData = new User(uId, uName, uEmail, uUrlPicture, selectedRestaurantId, selectedRestaurantName, selectionDate, likedRestaurantsIds);

        return userFromData;
    }

    // Get restaurant data from firestore with given document and create restaurant
    public static Restaurant getRestaurantFromDatabaseDocument(QueryDocumentSnapshot document) {
        String rId = Objects.requireNonNull(document.getData().get("id")).toString();
        String rName = Objects.requireNonNull(document.getData().get("name")).toString();
        long rDistance = (long) document.getData().get("distance");
        List<Photo> rPhotos = getPhotos(document);
        String rNationality = Objects.requireNonNull(document.getData().get("nationality")).toString();
        String rAddress = Objects.requireNonNull(document.getData().get("address")).toString();
        double rRating = (double) document.getData().get("rating");
        String rOpeningInformation = getOpeningInformation(document);
        int rLikesCount = Integer.parseInt(Objects.requireNonNull(document.getData().get("likesCount")).toString());
        String rPhoneNumber = document.getData().get("phoneNumber") != null ? document.getData().get("phoneNumber").toString() : "";
        String rWebsite = document.getData().get("website") != null ?  document.getData().get("website").toString() : "";
        Geometry rGeometry = getGeometry(document);

        // List<User> rSelectors = (List<User>) document.getData().get("selectors");
        List<User> rSelectors = null;

        Restaurant restaurantFromData = new Restaurant(rId, rName, rDistance, rPhotos, rNationality, rAddress,
                rRating, rOpeningInformation, rLikesCount, rPhoneNumber, rWebsite, rGeometry, rSelectors);

        return restaurantFromData;
    }


    public static String getOpeningInformation(QueryDocumentSnapshot document) {
        String openingInformation = "";
        if (document.getData().get("openingHours") != null) {
            // Possibility of 2 opening and closing periods in a day
            String closingTime1 = "";
            String openingTime1 = "";
            String closingTime2 = "";
            String openingTime2 = "";

            boolean openNow = (boolean) ((Map<String, Object>) document.getData().get("openingHours")).get("openNow");

            // TODO : To be deleted and replaced by method below
            openingInformation = openNow ? "open" : "closed";
            //

            // TODO : Replacement getting and displaying more info from API
            long currentDayOfWeek = CalendarUtils.getCurrentDayOfWeek();
            String currentTime = CalendarUtils.getCurrentTime();

            // Get the list of opening periods
            ArrayList periodsList = (ArrayList) ((Map<String, Object>) document.getData().get("openingHours")).get("periods");

            // Get details for each period p
            for (int i = 0; i < periodsList.size(); i++) {
                long pClosingDay = (long) ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("close")).get("day");
                String pClosingTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("close")).get("time").toString();
                long pOpeningDay = (long) ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("open")).get("day");
                String pOpeningTime = ((Map<String, Object>) ((Map<String, Object>) periodsList.get(i)).get("open")).get("time").toString();

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


    public static List<Photo> getPhotos(QueryDocumentSnapshot document) {
        List<Photo> photosList = null;

        // Get photos from document
        ArrayList photos = (ArrayList) document.getData().get("photos");
        if (photos != null) {
            photosList = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                // Get information for each photo
                long pHeight = (long) ((Map<Photo, Object>) photos.get(i)).get("height");
                long pWidth = (long) ((Map<Photo, Object>) photos.get(i)).get("width");
                String pPhotoReference = ((Map<Photo, Object>) photos.get(i)).get("photoReference").toString();
                List<String> htmlAttributions = new ArrayList<>();
                ArrayList pHtmlAttributions = (ArrayList) ((Map<Photo, Object>) photos.get(i)).get("htmlAttributions");
                if (pHtmlAttributions.size() != 0) {
                    for (int j = 0; j < pHtmlAttributions.size(); j++) {
                        htmlAttributions.add(pHtmlAttributions.get(j).toString());
                    }
                }
                // Create photo object and add it to the list
                Photo photoToAdd = new Photo(pPhotoReference, pHtmlAttributions, pHeight, pWidth);
                photosList.add(photoToAdd);
            }
        }

        return photosList;
    }

    public static Geometry getGeometry(QueryDocumentSnapshot document) {
        // Get lat and lng from document
        double lat = (double) ((Map<String, Object>) (Objects.requireNonNull(((Map<String, Object>) Objects.requireNonNull(document.getData().get("geometry"))).get("location")))).get("lat");
        double lng = (double) ((Map<String, Object>) (((Map<String, Object>) document.getData().get("geometry")).get("location"))).get("lng");
        // Create location
        Location location = new Location(lat, lng);
        // Create and return geometry
        return new Geometry(location);
    }

}
