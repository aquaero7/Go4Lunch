package com.example.go4lunch.manager;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.repository.RestaurantRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    // Create restaurant in Firestore

    public void createRestaurant(String id, String name, List<Photo> photos, String address,
                                 double rating, OpeningHours openingHours, String phoneNumber,
                                 String website, Geometry geometry) {

        restaurantRepository.createRestaurant(id, name, photos, address, rating,
                openingHours, phoneNumber, website, geometry);
    }

    // Get the restaurants list from Firestore
    public void getRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        restaurantRepository.getRestaurantsList(listener);
    }

    // Get the restaurant data from Firestore and cast it to a Restaurant model Object
    public Task<Restaurant> getRestaurantData(String id){
        return restaurantRepository.getRestaurantData(id)
                .continueWith(task -> task.getResult().toObject(Restaurant.class));
    }

    /* Clear the restaurants collection
    public void clearRestaurantsCollection() {
        restaurantRepository.clearRestaurantsCollection();
    }
    */

}
