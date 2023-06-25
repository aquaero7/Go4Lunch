package com.example.go4lunch.manager;

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
import com.example.go4lunch.repository.RestaurantRepository;
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

public class RestaurantManager {

    /** DEFAULT VALUES **/
    private final String DEFAULT_RADIUS = "1"; // Distance in km
    private final double DEF_LATITUDE = 0;   // 48.8566;//Paris 48.7258;//VLB 43.0931;//SFLP 48.5959;//SLT
    private final double DEF_LONGITUDE = 0;  //  2.3522;//Paris  2.1252;//VLB  5.8392;//SFLP  2.5810;//SLT
    /********************/

    private static volatile RestaurantManager instance;
    // private final RestaurantRepository restaurantRepository;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListWithDistance = new ArrayList<>();
    private LatLng home;

    private RestaurantManager() {
        // restaurantRepository = RestaurantRepository.getInstance();
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

    /*
    // Create restaurant in Firestore
    public void createRestaurant(String id, String name, List<Photo> photos, String address,
                                 double rating, OpeningHours openingHours, String phoneNumber,
                                 String website, Geometry geometry) {

        restaurantRepository.createRestaurant(id, name, photos, address, rating,
                openingHours, phoneNumber, website, geometry);
    }

    // Get the restaurants list from Firestore
    public void getRestaurantsList(OnCompleteListener<QuerySnapshot> listener) {
        restaurantRepository.getRestaurantsList(listener);
    }

    // Get the restaurant data from Firestore and cast it to a Restaurant model Object
    public Task<Restaurant> getRestaurantData(String id){
        return restaurantRepository.getRestaurantData(id)
                .continueWith(task -> task.getResult().toObject(Restaurant.class));
    }
    */


    @SuppressWarnings("MissingPermission")  // Permissions already checked in AuthActivity
    public void fetchCurrentLocationAndRestaurants(Activity activity, String apiKey, String radius) {
        // Get current location from API
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            // Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        // Get the last known location...
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        fetchRestaurants(apiKey, radius);
                    } else {
                        // Get the updated current location...
                        // Setup parameters of location request
                        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build();
                        // Create callback to handle location result
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                for (Location location : locationResult.getLocations()) {
                                    if (location != null) {
                                        // Get the current location...
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        // ...and stop location updates as soon of current location is got
                                        fusedLocationProviderClient.removeLocationUpdates(this);
                                        // Initialize home
                                        home = new LatLng(latitude, longitude);
                                        fetchRestaurants(apiKey, radius);
                                    }
                                }
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
                else {
                    Log.w("getCurrentLocation", "Exception: %s", task.getException());
                }
            });

        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }
    }

    public void fetchRestaurants(String apiKey, String radius) {
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
                    Log.w("RestaurantManager", "Nearby restaurants list not empty");
                    restaurantsList.clear();
                    for (Restaurant nearbyRestaurant : nearbyRestaurants) {
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
                                Log.w("RestaurantManager", t.getMessage(), t);
                            }
                        });
                    }
                    restaurantsListWithDistance = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
                    DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistance);

                } else {
                    Log.w("RestaurantManager", "Empty nearby restaurants list");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GmapsRestaurantPojo> call1, @NonNull Throwable t) {
                // Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                Log.w("RestaurantManager", t.getMessage(), t);
            }
        });
    }

    public LatLng getCurrentLocation() {
        return home;
    }

    public List<RestaurantWithDistance> getRestaurants() {
        return restaurantsListWithDistance;
    }

    public String getDefaultRadius() {
        return DEFAULT_RADIUS;
    }

    public LatLng getDefaultLocation() {
        return new LatLng(DEF_LATITUDE, DEF_LONGITUDE);
    }

}
