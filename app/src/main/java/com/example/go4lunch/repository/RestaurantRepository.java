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

    // Constructor
    public void createRestaurant(String id, String name, List<Photo> photos, String address,
                                 double rating, OpeningHours openingHours, String phoneNumber,
                                 String website, Geometry geometry) {

        Restaurant restaurantToCreate = new Restaurant(id, name, photos, address, rating,
                openingHours, phoneNumber, website, geometry);

        getRestaurantsCollection().document(id).set(restaurantToCreate);
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
