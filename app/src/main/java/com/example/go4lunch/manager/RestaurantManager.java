package com.example.go4lunch.manager;

import androidx.annotation.NonNull;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.repository.RestaurantRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RestaurantManager {

    private static volatile RestaurantManager instance;
    private final RestaurantRepository restaurantRepository;

    private RestaurantManager() {
        restaurantRepository = RestaurantRepository.getInstance();
    }

    public static RestaurantManager getInstance() {
        RestaurantManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantManager();
            }
            return instance;
        }
    }

    // Method for database constructor
    public void createRestaurant(String id, String name, long distance, List<Photo> photos, String nationality,
                                 String address, double rating, OpeningHours openingHours, int likesCount,
                                 String phoneNumber, String website, Geometry geometry, List<User> selectors) {

        restaurantRepository.createRestaurant(id, name, distance, photos, nationality, address, rating,
                                                openingHours, likesCount, phoneNumber, website, geometry, selectors);
    }

    // Method for API constructor
    public void createRestaurant(String id, String name, long distance, List<Photo> photos, String nationality,
                                 String address, double rating, OpeningHours openingHours,
                                 String phoneNumber, String website, Geometry geometry) {

        restaurantRepository.createRestaurant(id, name, distance, photos, nationality, address, rating,
                openingHours, phoneNumber, website, geometry);
    }


    // TODO : For test. To be deleted
    public void createRestaurant(){
        restaurantRepository.createRestaurant();
    }

    // TODO : For test. To be deleted
    public void createRestaurant(String id, String name) {
        restaurantRepository.createRestaurant(id, name);
    }


    public static void getRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        // Get the restaurants list from Firestore
        RestaurantRepository.getRestaurantsList(listener);
    }


    public static Task<Restaurant> getRestaurantData(String id){
        // Get the restaurant data from Firestore and cast it to a User model Object
        return RestaurantRepository.getRestaurantData(id).continueWith(task -> task.getResult().toObject(Restaurant.class));
    }

}
