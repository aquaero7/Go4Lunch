package com.example.go4lunch.repository;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    // Firestore
    // private static final String COLLECTION_RESTAURANTS = "restaurants";

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

    /*
    // Get the Collection Reference
    private CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANTS);
    }

    // Create restaurant in Firestore
    public void createRestaurant(String id, String name, List<Photo> photos, String address,
                                 double rating, OpeningHours openingHours, String phoneNumber,
                                 String website, Geometry geometry) {

        Restaurant restaurantToCreate = new Restaurant(id, name, photos, address, rating,
                openingHours, phoneNumber, website, geometry);

        getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    // Get restaurants list from Firestore
    public void getRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        getRestaurantsCollection().get().addOnCompleteListener(listener);
    }

    // Get restaurant data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(String id) {
        if(id != null){
            return getRestaurantsCollection().document(id).get();
        } else {
            return null;
        }
    }
    */


}
