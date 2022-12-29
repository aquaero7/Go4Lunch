package com.example.go4lunch.repository;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.OpeningHours;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
    private CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANTS);
    }

    // Create restaurant in Firestore

    // Method for database constructor
    public void createRestaurant(String id, String name, int distance, String imageUrl, String nationality,
                                 String address, double rating, OpeningHours openingHours, int likesCount,
                                 String phoneNumber, String website, List<User> selectors) {

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, imageUrl, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);

    }

    // Method for API constructor
    public void createRestaurant(String id, String name, int distance, String imageUrl, String nationality,
                                 String address, double rating, OpeningHours openingHours,
                                 String phoneNumber, String website) {

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, imageUrl, nationality,
                address, rating, openingHours, phoneNumber, website);

        getRestaurantsCollection().document(id).set(restaurantToCreate);

    }

    // TODO : For test. To be deleted
    public void createRestaurant() {

        String id = "ID1";  // TODO : Get data from API
        String name = "Name for test";  // TODO : Get data from API
        int distance = 0;  // TODO : Get data from API
        String urlImage = "";  // TODO : Get data from API
        String nationality = "";  // TODO : Get data from API
        String address = "";  // TODO : Get data from API
        double rating = 0.0;  // TODO : Get data from API
        OpeningHours openingHours = null;  // TODO : Get data from API
        int likesCount = 0;  // TODO : Get data from database
        String phoneNumber = "";  // TODO : Get data from API
        String website = "";  // TODO : Get data from API
        List<User> selectors = null;    // TODO : Get data from database

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, urlImage, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);    // Create ou update a document with nothing given

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

        // String id = "ID1";  // TODO : Get data from API
        // String name = "";  // TODO : Get data from API
        int distance = 0;  // TODO : Get data from API
        String urlImage = "";  // TODO : Get data from API
        String nationality = "";  // TODO : Get data from API
        String address = "";  // TODO : Get data from API
        double rating = 0.0;  // TODO : Get data from API
        OpeningHours openingHours = null;  // TODO : Get data from API
        int likesCount = 0;  // TODO : Get data from database
        String phoneNumber = "";  // TODO : Get data from API
        String website = "";  // TODO : Get data from API
        List<User> selectors = null;    // TODO : Get data from database

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, urlImage, nationality,
                address, rating, openingHours, likesCount, phoneNumber, website, selectors);

        getRestaurantsCollection().document(id).set(restaurantToCreate);    // Create or update a document with given ID and nane

    }


    /* Get Restaurant Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(){
        String id = "ID1";  // TODO : To be defined
        if(id != null){
            return this.getRestaurantsCollection().document(id).get();
        }else{
            return null;
        }
    }
    */

}
