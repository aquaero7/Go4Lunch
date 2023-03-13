package com.example.go4lunch.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    // Firestore
    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String COLLECTION_USERS = "users";

    public RestaurantRepository() {
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    // Get the Collection Reference
    private static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANTS);
    }

    // Create restaurant in Firestore

    // Method for database constructor
    public void createRestaurant(String id, String name, long distance, List<Photo> photos, String nationality,
                                 String address, double rating, OpeningHours openingHours, int likesCount,
                                 String phoneNumber, String website, Geometry geometry, List<User> selectors) {

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, photos, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, geometry, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);

    }

    // Method for API constructor
    public void createRestaurant(String id, String name, long distance, List<Photo> photos, String nationality,
                                 String address, double rating, OpeningHours openingHours,
                                 String phoneNumber, String website, Geometry geometry) {

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, photos, nationality,
                address, rating, openingHours, phoneNumber, website, geometry);

        getRestaurantsCollection().document(id).set(restaurantToCreate);

    }


    // TODO : For test. To be deleted
    public void createRestaurant() {
        /*
        String id = "ID1";  // TODO : Get data from API
        String name = "Name for test";  // TODO : Get data from API
        long distance = 0;  // TODO : Get data from API
        List<Photo> photos = null;  // TODO : Get data from API
        String nationality = "";  // TODO : Get data from API
        String address = "";  // TODO : Get data from API
        double rating = 0.0;  // TODO : Get data from API
        OpeningHours openingHours = null;  // TODO : Get data from API
        int likesCount = 0;  // TODO : Get data from database
        String phoneNumber = "";  // TODO : Get data from API
        String website = "";  // TODO : Get data from API
        Geometry geometry = null; // TODO : Get data from API
        List<User> selectors = null;    // TODO : Get data from database

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, photos, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, geometry, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);    // Create ou update a document with nothing given
        */

        // getRestaurantsCollection().add(restaurantToCreate);  // Create a document with new automatic ID
        // getRestaurantsCollection().document(id).update("name", name);    // Update a field of a document with given ID


        /* If the restaurant already exist in Firestore, we get his hole data
        Task<DocumentSnapshot> restaurantData = getRestaurantData();
        restaurantData.addOnSuccessListener(documentSnapshot -> {
            // this.getRestaurantsCollection().document(id).set(restaurantToCreate);   // Create ou update a document with given ID
            this.getRestaurantsCollection().add(restaurantToCreate);  // Create a document with new automatic ID
            // this.getRestaurantsCollection().document(id).update("name", name);    // Update a field of a document with given ID
        });
        */
    }

    // TODO : For test. To be deleted
    public void createRestaurant(String id, String name) {
        /*
        // String id = "ID1";  // TODO : Get data from API
        // String name = "";  // TODO : Get data from API
        long distance = 0;  // TODO : Get data from API
        List<Photo> photos = null;  // TODO : Get data from API
        String nationality = "";  // TODO : Get data from API
        String address = "";  // TODO : Get data from API
        double rating = 0.0;  // TODO : Get data from API
        OpeningHours openingHours = null;  // TODO : Get data from API
        int likesCount = 0;  // TODO : Get data from database
        String phoneNumber = "";  // TODO : Get data from API
        String website = "";  // TODO : Get data from API
        Geometry geometry = null; // TODO : Get data from API
        List<User> selectors = null;    // TODO : Get data from database

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, photos, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, geometry, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);    // Create or update a document with given ID and nane
        */
    }


    // Get restaurants list from Firestore
    public static void getRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        getRestaurantsCollection().get().addOnCompleteListener(listener);
    }


    // Get restaurant data from Firestore
    public static Task<DocumentSnapshot> getRestaurantData(String id) {
        if(id != null){
            return getRestaurantsCollection().document(id).get();
        } else {
            return null;
        }
    }

    // Clear the restaurants collection
    public static void clearRestaurantsCollection() {
        getRestaurantsList(task -> {
            if (task.isSuccessful()) {
                // Get and delete each document in restaurants collection
                for (QueryDocumentSnapshot document : task.getResult()) {
                    getRestaurantsCollection().document(document.getId()).delete();
                }
            } else {
                Log.d("RestaurantRepository", "Error getting documents: ", task.getException());
            }
        });
    }

}
