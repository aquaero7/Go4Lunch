package com.example.go4lunch.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.go4lunch.R;
import com.example.go4lunch.api.GmapsApiClient;
import com.example.go4lunch.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.api.GmapsRestaurantPojo;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsApisUtils {

    private static final double DEF_LATITUDE = 0;   // 48.8566;//Paris 48.7258;//VLB 43.0931;//SFLP 48.5959;//SLT
    private static final double DEF_LONGITUDE = 0;  //VLB  //  2.3522;//Paris  2.1252;//VLB  5.8392;//SFLP  2.5810;//SLT
    private static final int DEFAULT_RADIUS = 1000; // Distance in meters
    private static List<Restaurant> restaurantsList = new ArrayList<>();
    private static double latitude;
    private static double longitude;


    public static int getDefaultRadius() {
        return DEFAULT_RADIUS;
    }

    // USED WITH SOLUTION 1 :
    @SuppressWarnings("MissingPermission")
    // Permissions already checked in checkPermissionsAndLoadMap() method, called in onResume() method in MapsViewFragment
    public static LatLng getDeviceLocation(boolean locationPermissionsGranted, FusedLocationProviderClient fusedLocationProviderClient, Activity activity) {
        try {
            if (locationPermissionsGranted) {
                // Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
                locationResult.addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            latitude = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();
                        } else {
                            latitude = DEF_LATITUDE;
                            longitude = DEF_LONGITUDE;
                            Toast.makeText(activity, R.string.info_no_current_location, Toast.LENGTH_SHORT).show();
                            Log.w("getDeviceLocation", "Exception: %s", task.getException());
                        }
                    }
                    else {
                        Log.w("getDeviceLocation", "Exception: %s", task.getException());
                    }
                });
            } else {
                latitude = DEF_LATITUDE;
                longitude = DEF_LONGITUDE;
                Toast.makeText(activity, R.string.info_no_permission, Toast.LENGTH_SHORT).show();
                Log.w("getDeviceLocation", "Permissions not granted");
            }
        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }

        return new LatLng(latitude, longitude);
    }


    // Get restaurants list from API
    public static List<Restaurant> getRestaurantsFromApi(LatLng latLng, String apiKey, Context context) {

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
        long distance = DataProcessingUtils.calculateRestaurantDistance(nearbyRestaurant, latLng);

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

                /** For solution A : Getting data from API in ListViewFragment */
                // Add restaurant to the list
                restaurantsList.add(new Restaurant(id, name, distance, photos,
                        nationality, address, rating, openingHours, phoneNumber, website, geometry));

                /** For solution B : Getting data from Firestore in ListViewFragment
                TODO : To be deleted if replaced by list from API in ListViewFragment ??? */
                // Create ou update restaurant in Firebase
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
        // Initialize the AutocompleteSupportFragment.
        // autocompleteCardView = binding.includedToolbar.includedAutocompleteCardView.autocompleteCardView;
        // autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Specify the type values of place data to return.
        autocompleteFragment.setTypesFilter(Arrays.asList("restaurant"));
        // Specify the country of place data to return.
        autocompleteFragment.setCountries("FR");
    }

    public static void configureAutocompleteSupportFragment(AutocompleteSupportFragment autocompleteFragment, Activity activity) {
        // Specify the limitation to only show results within the defined region
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        LatLng home = MapsApisUtils.getDeviceLocation(true, fusedLocationProviderClient, activity);
        // int radius = MapsApisUtils.getDefaultRadius();   TODO : To be deleted
        LatLngBounds latLngBounds = DataProcessingUtils.calculateBounds(home, DEFAULT_RADIUS);
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                LatLng latLng = place.getLatLng();
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                Log.i("MainActivity", "Place: " + place.getName() + ", " + place.getId() + ", " + latitude + ", " + longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("MainActivity", "An error occurred: " + status);
            }

        });
    }


}
