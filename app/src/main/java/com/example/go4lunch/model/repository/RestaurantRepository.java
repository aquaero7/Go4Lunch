package com.example.go4lunch.model.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.api.GmapsApiClient;
import com.example.go4lunch.model.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.model.api.GmapsRestaurantPojo;
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

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
    private final MutableLiveData<List<RestaurantWithDistance>> restaurantsMutableLiveData;
    private final MutableLiveData<RestaurantWithDistance> restaurantDetailsMutableLiveData;
    private final List<Restaurant> restaurantsList;
    private List<RestaurantWithDistance> restaurantsListWithDistance;
    private List<RestaurantWithDistance> restaurantsToDisplay;
    private RestaurantWithDistance restaurant;
    private boolean filterStatus;
    private boolean restaurantIsSelected;

    private RestaurantRepository() {
        restaurantsMutableLiveData = new MutableLiveData<>();
        restaurantDetailsMutableLiveData = new MutableLiveData<>();

        restaurantsList = new ArrayList<>();
        restaurantsListWithDistance = new ArrayList<>();
        restaurantsToDisplay = new ArrayList<>();

        filterStatus = false;
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
                        String rId = nearbyRestaurant.getRid();
                        String name = nearbyRestaurant.getName();
                        String address = nearbyRestaurant.getAddress(); // Not implemented by this API
                        String phoneNumber = nearbyRestaurant.getPhoneNumber(); // Not implemented by this API
                        String website = nearbyRestaurant.getWebsite(); // Not implemented by this API
                        double rating = nearbyRestaurant.getRating();
                        OpeningHours openingHours = nearbyRestaurant.getOpeningHours();
                        List<Photo> photos = nearbyRestaurant.getPhotos();
                        Geometry geometry = nearbyRestaurant.getGeometry();

                        // Add restaurant to current restaurants list
                        restaurantsList.add(new Restaurant(rId, name, photos, address, rating, openingHours,
                                phoneNumber, website, geometry));
                    }

                    restaurantsListWithDistance = Utils.updateRestaurantsListWithDistances(restaurantsList, home);
                    Utils.sortByDistanceAndName(restaurantsListWithDistance);
                    // Populate the LiveData
                    restaurantsMutableLiveData.setValue(restaurantsListWithDistance);
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
    }

    public void fetchRestaurantDetails(RestaurantWithDistance nearbyRestaurant, String apiKey) {

        // Call Place Details API //////////////////////////////////////////////////
        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(
                nearbyRestaurant.getRid(),
                "formatted_address,formatted_phone_number,website,opening_hours",
                apiKey
        );
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                // Update fields with detail information
                nearbyRestaurant.setAddress(restaurantDetails.getAddress());
                nearbyRestaurant.setOpeningHours(restaurantDetails.getOpeningHours());  // Updated with full information
                nearbyRestaurant.setPhoneNumber(restaurantDetails.getPhoneNumber());
                nearbyRestaurant.setWebsite(restaurantDetails.getWebsite());

                // Populate the LiveData
                restaurantDetailsMutableLiveData.setValue(nearbyRestaurant);
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                Log.w("RestaurantRepository", t.getMessage(), t);
            }
        });
    }

    public MutableLiveData<List<RestaurantWithDistance>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    public MutableLiveData<RestaurantWithDistance> getRestaurantDetailsMutableLiveData() {
        return restaurantDetailsMutableLiveData;
    }

    public String getDefaultRadius() {
        return DEFAULT_RADIUS;
    }

    public void setRestaurant(RestaurantWithDistance mRestaurant) {
        restaurant = mRestaurant;
    }

    public RestaurantWithDistance getRestaurant() {
        return restaurant;
    }

    public void setRestaurantSelected(boolean selected) {
        restaurantIsSelected = selected;
    }

    public boolean isRestaurantSelected() {
        return restaurantIsSelected;
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantsListWithDistance;
    }

    public void setFilterStatus(boolean status) {
        filterStatus = status;
    }

    public List<RestaurantWithDistance> getRestaurantsToDisplay() {
        return restaurantsToDisplay;
    }

    public void setRestaurantsToDisplay(List<RestaurantWithDistance> restaurants) {
        restaurantsToDisplay.clear();
        restaurantsToDisplay.addAll(restaurants);
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
        restaurantsListWithDistance = Utils.updateRestaurantsListWithDistances(restaurantsList, home);
        Utils.sortByDistanceAndName(restaurantsListWithDistance);
        // Populate the LiveData
        restaurantsMutableLiveData.setValue(restaurantsListWithDistance);
        //

        return restaurantsMutableLiveData;
    }
    */

}
