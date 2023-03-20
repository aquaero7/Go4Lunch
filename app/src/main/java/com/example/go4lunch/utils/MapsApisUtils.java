package com.example.go4lunch.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.go4lunch.R;
import com.example.go4lunch.api.GmapsApiClient;
import com.example.go4lunch.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.api.GmapsRestaurantPojo;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsApisUtils extends FragmentActivity {

    private static final double DEF_LATITUDE = 0;   // 48.8566;//Paris 48.7258;//VLB 43.0931;//SFLP 48.5959;//SLT
    private static final double DEF_LONGITUDE = 0;  //VLB  //  2.3522;//Paris  2.1252;//VLB  5.8392;//SFLP  2.5810;//SLT
    private static final int DEFAULT_RADIUS = 1000; // Distance in meters
    private static boolean locationPermissionsGranted;
    private static List<Restaurant> restaurantsList = new ArrayList<>();
    private static LatLng home;


    public static int getDefaultRadius() {
        return DEFAULT_RADIUS;
    }

    public static boolean arePermissionsGranted() {
        return locationPermissionsGranted;
    }

    public static LatLng getHome() {
        return home;
    }

    public static void setPermissions(boolean granted) {
        locationPermissionsGranted = granted;
    }

    public static void setHome(LatLng latLng) {
        home = latLng;
    }

    public static void setRestaurantsList(List<Restaurant> restaurants) {
        restaurantsList = restaurants;
    }



    // Get restaurants list from API
    public static List<Restaurant> getRestaurantsFromApi(Context context, LatLng latLng, String apiKey) {

        // Call Place Nearby Search API
        Call<GmapsRestaurantPojo> call1 = GmapsApiClient.getApiClient().getPlaces("restaurant", latLng.latitude + "," + latLng.longitude, DEFAULT_RADIUS, apiKey);
        call1.enqueue(new Callback<GmapsRestaurantPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Response<GmapsRestaurantPojo> response1) {
                GmapsRestaurantPojo nearPlaces = response1.body();
                List<Restaurant> nearbyRestaurants = nearPlaces.getNearRestaurants();

                // For each restaurant, get basic information ask for detailed information and add restaurant to the list
                if (nearbyRestaurants != null) {
                    Log.w("MAPViewFragment", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
                        getRestaurantDetailsFromApi(nearbyRestaurant, latLng, apiKey, context);

                    }
                } else {
                    Log.w("MapsApisUtils", "Empty nearby restaurants list");
                }
                //
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                Log.w("MapsApisUtils", t.getMessage(), t);
            }
        });
        return restaurantsList;
    }


    private static void getRestaurantDetailsFromApi(Restaurant nearbyRestaurant, LatLng latLng, String apiKey, Context context) {

        String id = nearbyRestaurant.getId();
        String name = nearbyRestaurant.getName();
        double rating = nearbyRestaurant.getRating();
        OpeningHours openingHours = nearbyRestaurant.getOpeningHours(); // Can be commented if openingHours come from nearby api
        List<Photo> photos = nearbyRestaurant.getPhotos();
        Geometry geometry = nearbyRestaurant.getGeometry();
        // long distance = DataProcessingUtils.calculateRestaurantDistance(nearbyRestaurant, latLng);
        long distance = 0;

        // Call Place Details API
        Call<GmapsRestaurantDetailsPojo> call2 = GmapsApiClient.getApiClient().getPlaceDetails(id,
                "formatted_address,formatted_phone_number,website,opening_hours",
                apiKey
        );
        call2.enqueue(new Callback<GmapsRestaurantDetailsPojo>() {
            @Override
            public void onResponse(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Response<GmapsRestaurantDetailsPojo> response2) {
                // Gson gson = new Gson();
                // String res = gson.toJson(response2.body());
                GmapsRestaurantDetailsPojo placeDetails = response2.body();
                Restaurant restaurantDetails = placeDetails.getRestaurantDetails();

                String nationality = "";    // TODO : Where to get this information ?
                String address = restaurantDetails.getAddress();
                String phoneNumber = restaurantDetails.getPhoneNumber();
                String website = restaurantDetails.getWebsite();
                OpeningHours openingHours = restaurantDetails.getOpeningHours();    // Can be commented to make openingHours come from nearby api

                /** Add restaurant to current restaurants list */
                restaurantsList.add(new Restaurant(id, name, distance, photos,
                        nationality, address, rating, openingHours, phoneNumber, website, geometry));

                /** Create or update restaurant in Firebase */
                RestaurantManager.getInstance().createRestaurant(id, name, distance, photos,
                        nationality, address, rating, openingHours, phoneNumber, website, geometry);
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantDetailsPojo> call2, @NonNull Throwable t) {
                Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                Log.w("MapsApisUtils", t.getMessage(), t);
            }
        });
    }

    public static void initializeAutocompleteSupportFragment(AutocompleteSupportFragment autocompleteFragment) {
        // Specify the types of place data to return.
        // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.ID));
        // Specify the type values of place data to return.
        autocompleteFragment.setTypesFilter(Collections.singletonList("restaurant"));
        // Specify the country of place data to return.
        autocompleteFragment.setCountries("FR");
    }




}
