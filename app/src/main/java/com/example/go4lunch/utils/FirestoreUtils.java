package com.example.go4lunch.utils;

import android.util.Log;

import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.Location;
import com.example.go4lunch.model.api.OpenClose;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Period;
import com.example.go4lunch.model.api.Photo;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreUtils {

    private static List<Restaurant> restaurantsList = new ArrayList<>();
    private static List<User> workmatesList = new ArrayList<>();
    private static List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();


    // Get current restaurants list
    public static List<Restaurant> getRestaurantsList() {
        return restaurantsList;
    }

    // Get current workmates list   // TODO
    public static List<User> getWorkmatesList() {
        return workmatesList;
    }

    // Get current liked restaurants list   // TODO
    public static List<LikedRestaurant> getLikedRestaurantsList() {
        return likedRestaurantsList;
    }


    // Get user data from Firestore with given document and create user
    public static User getUserFromDatabaseDocument(QueryDocumentSnapshot document) {
        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
        String uName = Objects.requireNonNull(document.getData().get("username")).toString();
        String uEmail = ((document.getData().get("userEmail")) != null) ? document.getData().get("userEmail").toString() : null;
        String uUrlPicture = ((document.getData().get("userUrlPicture")) != null) ? document.getData().get("userUrlPicture").toString() : null;
        String selectionId = ((document.getData().get("selectionId")) != null) ? document.getData().get("selectionId").toString() : null;
        String selectionDate = ((document.getData().get("selectionDate")) != null) ? document.getData().get("selectionDate").toString() : null;

        User userFromData = new User(uId, uName, uEmail, uUrlPicture, selectionId, selectionDate);

        return userFromData;
    }


    // Get restaurant data from Firestore with given document and create restaurant
    public static Restaurant getRestaurantFromDatabaseDocument(QueryDocumentSnapshot document) {
        String rId = Objects.requireNonNull(document.getData().get("rid")).toString();
        String rName = Objects.requireNonNull(document.getData().get("name")).toString();
        List<Photo> rPhotos = getPhotos(document);
        String rAddress = Objects.requireNonNull(document.getData().get("address")).toString();
        double rRating = (double) document.getData().get("rating");
        OpeningHours rOpeningHours = getOpeningHours(document);
        String rPhoneNumber = document.getData().get("phoneNumber") != null ? document.getData().get("phoneNumber").toString() : "";
        String rWebsite = document.getData().get("website") != null ?  document.getData().get("website").toString() : "";
        Geometry rGeometry = getGeometry(document);

        Restaurant restaurantFromData = new Restaurant(rId, rName, rPhotos, rAddress, rRating,
                rOpeningHours, rPhoneNumber, rWebsite, rGeometry);

        return restaurantFromData;
    }


    public static OpeningHours getOpeningHours(QueryDocumentSnapshot document) {

        OpeningHours openingHours;
        Object openingHoursObject = document.getData().get("openingHours");

        if (openingHoursObject != null) {
            // Initialize openNow
            boolean openNow = (boolean) ((Map<String, Object>) openingHoursObject).get("openNow");

            // Initialize periods
            ArrayList periodsList = (ArrayList) ((Map<String, Object>) openingHoursObject).get("periods");
            List<Period> periods = new ArrayList<>();

            if (periodsList != null) {
                // Get details for each period p
                for (int i = 0; i < periodsList.size(); i++) {
                    long pOpeningDay;
                    String pOpeningTime;
                    long pClosingDay;
                    String pClosingTime;
                    OpenClose pOpen;
                    OpenClose pClose;
                    Period period;

                    Object openObject = ((Map<String, Object>) periodsList.get(i)).get("open");
                    Object closeObject = ((Map<String, Object>) periodsList.get(i)).get("close");
                    if (openObject != null) {
                        pOpeningDay = (long) ((Map<String, Object>) (openObject)).get("day");
                        pOpeningTime = Objects.requireNonNull(((Map<String, Object>) (openObject)).get("time")).toString();
                        pOpen = new OpenClose(pOpeningDay, pOpeningTime);
                    } else {
                        pOpen = null;
                    }
                    if (closeObject != null) {
                        pClosingDay = (long) ((Map<String, Object>) (closeObject)).get("day");
                        pClosingTime = Objects.requireNonNull(((Map<String, Object>) (closeObject)).get("time")).toString();
                        pClose = new OpenClose(pClosingDay, pClosingTime);
                    } else {
                        pClose = null;
                    }

                    period = new Period(pClose, pOpen);
                    periods.add(period);
                }

            } else {
                periods = null;
            }

            // Initialize weekdayText
            ArrayList weekdayTextList = (ArrayList) ((Map<String, Object>) openingHoursObject).get("weekdayText");
            List<String> weekdayText = new ArrayList<>();

            if (weekdayTextList != null) {
                // Get details for each weekday text
                for (int i = 0; i < weekdayTextList.size(); i++) {
                    String item = weekdayTextList.get(i).toString();
                    weekdayText.add(item);
                }

            } else {
                weekdayText = null;

            }

            openingHours = new OpeningHours(openNow, periods, weekdayText);

        } else {
            openingHours = null;
        }

        return openingHours;
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


    // Get liked restaurant data from Firestore with given document and create liked restaurant
    public static LikedRestaurant getLikedRestaurantFromDatabaseDocument(QueryDocumentSnapshot document) {
        String id = Objects.requireNonNull(document.getId());
        String rId = Objects.requireNonNull(document.getData().get("rid")).toString();
        String uId = Objects.requireNonNull(document.getData().get("uid")).toString();
        LikedRestaurant likedRestaurantFromData = new LikedRestaurant(id, rId, uId);

        return likedRestaurantFromData;
    }


    // Get restaurants list from Firestore
    public static List<Restaurant> getRestaurantsListFromDatabaseDocument() {
        RestaurantManager.getRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get restaurants list
                    restaurantsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> restaurantData = document.getData(); // Map data for debug.
                        Restaurant restaurantToAdd = FirestoreUtils.getRestaurantFromDatabaseDocument(document);
                        restaurantsList.add(restaurantToAdd);
                    }
                }
            } else {
                Log.d("FirestoreUtils", "Error getting documents: ", task.getException());
            }
        });
        return restaurantsList;
    }


    // Get workmates list from Firestore
    public static List<User> getWorkmatesListFromDatabaseDocument() {
        UserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get users list
                    workmatesList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // Map data for debug.
                        User workmateToAdd = FirestoreUtils.getUserFromDatabaseDocument(document);
                        workmatesList.add(workmateToAdd);
                    }
                }
            } else {
                Log.w("FirestoreUtils", "Error getting documents: ", task.getException());
            }
        });
        return workmatesList;
    }


    // Get liked restaurants list from Firestore
    public static List<LikedRestaurant> getLikedRestaurantsListFromDatabaseDocument() {
        LikedRestaurantManager.getLikedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get liked restaurants list
                    likedRestaurantsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> likedRestaurantData = document.getData(); // Map data for debug.
                        LikedRestaurant likedRestaurantToAdd = FirestoreUtils.getLikedRestaurantFromDatabaseDocument(document);
                        likedRestaurantsList.add(likedRestaurantToAdd);
                    }
                }
            } else {
                Log.d("FirestoreUtils", "Error getting documents: ", task.getException());
            }
        });
        return likedRestaurantsList;
    }

    public static User getCurrentUserFromDatabaseDocument() {
        final User[] currentUser = new User[1];
        UserManager.getInstance().getCurrentUserData()
                .addOnSuccessListener(user -> {
                    String uId = user.getUid();
                    String uName = user.getUsername();
                    String uEmail = user.getUserEmail();
                    String uUrlPicture = user.getUserUrlPicture();
                    String selectionId = user.getSelectionId();
                    String selectionDate = user.getSelectionDate();
                    currentUser[0] = new User(uId, uName, uEmail, uUrlPicture, selectionId, selectionDate);
                })

                .addOnFailureListener(e -> Log.w("FirestoreUtils", e.getMessage()));

        return currentUser[0];
    }

}
