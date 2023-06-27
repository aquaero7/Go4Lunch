package com.example.go4lunch.repository;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.api.GmapsApiClient;
import com.example.go4lunch.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.api.GmapsRestaurantPojo;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    /** DEFAULT VALUES **/
    private final String DEFAULT_RADIUS = "1"; // Distance in km
    /********************/

    private static volatile RestaurantRepository instance;
    private MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListWithDistance = new ArrayList<>();

    private RestaurantRepository() {
        restaurantsMutableLiveData = new MutableLiveData<>();
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        /*
        synchronized(RestaurantHelperUnused.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
        */
        else {
            instance = new RestaurantRepository();
            return instance;
        }
    }

    public void fetchRestaurants(LatLng home, String radius, String apiKey) {
        // Call Place Nearby Search API ////////////////////////////////////////////////////////////
        Call<GmapsRestaurantPojo> call1 = GmapsApiClient.getApiClient()
                .getPlaces("restaurant", home.latitude + "," + home.longitude, Integer.parseInt(radius)*1000, apiKey);
        call1.enqueue(new Callback<GmapsRestaurantPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                GmapsRestaurantPojo nearPlaces = response1.body();
                List<Restaurant> nearbyRestaurants = nearPlaces.getNearRestaurants();

                // For each restaurant, get basic information ask for detailed information and add restaurant to the list
                if (nearbyRestaurants != null) {
                    Log.w("RestaurantRepository", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
                        getRestaurantDetailsFromApi(nearbyRestaurant, apiKey);

                        /*
                        String rId = nearbyRestaurant.getRid();
                        String name = nearbyRestaurant.getName();
                        double rating = nearbyRestaurant.getRating();
                        OpeningHours openingHours = nearbyRestaurant.getOpeningHours(); // Can be commented if openingHours come from details api
                        List<Photo> photos = nearbyRestaurant.getPhotos();
                        Geometry geometry = nearbyRestaurant.getGeometry();

                        // Call Place Details API //////////////////////////////////////////////////
                        // getRestaurantDetailsFromApi(nearbyRestaurant, apiKey, context);
                        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(rId,
                                "formatted_address,formatted_phone_number,website,opening_hours",
                                apiKey
                        );
                        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
                            @Override
                            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                                String address = restaurantDetails.getAddress();
                                String phoneNumber = restaurantDetails.getPhoneNumber();
                                String website = restaurantDetails.getWebsite();
                                OpeningHours openingHours = restaurantDetails.getOpeningHours(); // Can be commented to make openingHours come from nearby api

                                // Add restaurant to current restaurants list
                                restaurantsList.add(new Restaurant(rId, name, photos, address, rating, openingHours,
                                        phoneNumber, website, geometry));
                            }

                            @Override
                            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                                // Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                                Log.w("RestaurantRepository", t.getMessage(), t);
                            }
                        });
                        */

                    }
                    // restaurantsListWithDistance = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
                    // DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistance);
                } else {
                    Log.w("RestaurantRepository", "Empty nearby restaurants list");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                // Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });

        //
        restaurantsListWithDistance = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
        DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistance);
        //
    }

    private void getRestaurantDetailsFromApi(Restaurant nearbyRestaurant, String apiKey) {
        String rId = nearbyRestaurant.getRid();
        String name = nearbyRestaurant.getName();
        double rating = nearbyRestaurant.getRating();
        OpeningHours openingHours = nearbyRestaurant.getOpeningHours(); // Can be commented if openingHours come from details api
        List<Photo> photos = nearbyRestaurant.getPhotos();
        Geometry geometry = nearbyRestaurant.getGeometry();

        // Call Place Details API //////////////////////////////////////////////////
        // getRestaurantDetailsFromApi(nearbyRestaurant, apiKey, context);
        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(rId,
                "formatted_address,formatted_phone_number,website,opening_hours",
                apiKey
        );
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                String address = restaurantDetails.getAddress();
                String phoneNumber = restaurantDetails.getPhoneNumber();
                String website = restaurantDetails.getWebsite();
                OpeningHours openingHours = restaurantDetails.getOpeningHours(); // Can be commented to make openingHours come from nearby api

                // Add restaurant to current restaurants list
                restaurantsList.add(new Restaurant(rId, name, photos, address, rating, openingHours,
                        phoneNumber, website, geometry));
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                restaurantsList.add(new Restaurant(rId, name, photos, null, rating, openingHours,
                        null, null, geometry));

                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(restaurantsListWithDistance);
        return restaurantsMutableLiveData;
    }

    public String getDefaultRadius() {
        return DEFAULT_RADIUS;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData(LatLng home, String radius, String apiKey) {
        restaurantsMutableLiveData = new MutableLiveData<>();

        if (radius == null) radius = DEFAULT_RADIUS;

        // Call Place Nearby Search API ////////////////////////////////////////////////////////////
        Call<GmapsRestaurantPojo> call1 = GmapsApiClient.getApiClient()
                .getPlaces("restaurant", home.latitude + "," + home.longitude, Integer.parseInt(radius)*1000, apiKey);
        call1.enqueue(new Callback<GmapsRestaurantPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                GmapsRestaurantPojo nearPlaces = response1.body();
                List<Restaurant> nearbyRestaurants = nearPlaces.getNearRestaurants();

                // For each restaurant, get basic information ask for detailed information and add restaurant to the list
                if (nearbyRestaurants != null) {
                    Log.w("RestaurantRepository", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
                        getRestaurantDetailsFromApi(nearbyRestaurant, apiKey);
                    }
                } else {
                    Log.w("RestaurantRepository", "Empty nearby restaurants list");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                // Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });

        //
        restaurantsListWithDistance = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
        DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistance);
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(restaurantsListWithDistance);
        //

        return restaurantsMutableLiveData;
    }
    */

}
