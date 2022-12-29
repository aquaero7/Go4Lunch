package com.example.go4lunch.manager;

import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.repository.RestaurantRepository;

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
    public void createRestaurant(String id, String name, int distance, String imageUrl, String nationality,
                                 String address, double rating, OpeningHours openingHours, int likesCount,
                                 String phoneNumber, String website, List<User> selectors) {

        restaurantRepository.createRestaurant(id, name, distance, imageUrl, nationality, address, rating,
                                                openingHours, likesCount, phoneNumber, website, selectors);
    }

    // Method for API constructor
    public void createRestaurant(String id, String name, int distance, String imageUrl, String nationality,
                                 String address, double rating, OpeningHours openingHours,
                                 String phoneNumber, String website) {

        restaurantRepository.createRestaurant(id, name, distance, imageUrl, nationality, address, rating,
                openingHours, phoneNumber, website);
    }


    // TODO : For test. To be deleted
    public void createRestaurant(){
        restaurantRepository.createRestaurant();
    }

    // TODO : For test. To be deleted
    public void createRestaurant(String id, String name) {
        restaurantRepository.createRestaurant(id, name);
    }

    /*
    public Task<Restaurant> getRestaurantData(){
        // Get the restaurant from Firestore and cast it to a User model Object
        return restaurantRepository.getRestaurantData().continueWith(task -> task.getResult().toObject(Restaurant.class)) ;
    }
    */

}
