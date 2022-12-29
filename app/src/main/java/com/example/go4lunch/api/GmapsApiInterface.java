package com.example.go4lunch.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GmapsApiInterface {

    String BASE_URL = "https://maps.googleapis.com/maps/api/";

    // API's endpoint
    @GET("place/nearbysearch/json?")
    // GmapsRestaurantPojo is POJO class to get the data from API
    // We use List<GmapsRestaurantPojo> in callback because the data in our API is starting from JSONArray
    Call<GmapsRestaurantPojo> getPlaces(@Query(value = "type", encoded = true) String type,
                                              @Query(value = "location", encoded = true) String location,
                                              @Query(value = "radius", encoded = true) int radius,
                                              @Query(value = "key", encoded = true) String key);

}
