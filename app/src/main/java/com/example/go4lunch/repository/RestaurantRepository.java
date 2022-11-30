package com.example.go4lunch.repository;

import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public void createRestaurant() {

        String id = "ID1";  // TODO : Get data from API
        String name = "";  // TODO : Get data from API
        int distance = 0;  // TODO : Get data from API
        String urlImage = "";  // TODO : Get data from API
        String nationality = "";  // TODO : Get data from API
        String address = "";  // TODO : Get data from API
        int favRating = 0;  // TODO : Get data from API
        String openingTime = "";  // TODO : Get data from API
        int likesCount = 0;  // TODO : Get data from API
        String phoneNumber = "";  // TODO : Get data from API
        String website = "";  // TODO : Get data from API

        Restaurant restaurantToCreate = new Restaurant(id, name, distance, urlImage, nationality,
                address, favRating, openingTime, likesCount, phoneNumber, website);

        getRestaurantsCollection().document(id).set(restaurantToCreate);    // Create ou update a document with given ID
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
