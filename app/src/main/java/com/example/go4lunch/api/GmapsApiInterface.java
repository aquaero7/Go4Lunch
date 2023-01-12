package com.example.go4lunch.api;

import com.example.go4lunch.model.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GmapsApiInterface {

    String BASE_URL = "https://maps.googleapis.com/maps/api/";

    // API's endpoints

    // GmapsRestaurantPojo is POJO class to get the data from API
    // We use List<GmapsRestaurantPojo> in callback because the data in our API is starting from JSONArray
    @GET("place/nearbysearch/json?")
    Call<GmapsRestaurantPojo> getPlaces(@Query(value = "type", encoded = true) String type,
                                        @Query(value = "location", encoded = true) String location,
                                        @Query(value = "radius", encoded = true) int radius,
                                        @Query(value = "key", encoded = true) String key);

    @GET("place/details/json?")
    Call<GmapsRestaurantDetailsPojo> getPlaceDetails(@Query(value = "place_id", encoded = true) String place_id,
                                                     @Query(value = "fields", encoded = true) String fields,
                                                     @Query(value = "key", encoded = true) String key);

}
