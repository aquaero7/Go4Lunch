package com.example.go4lunch.model.repository;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

public class LocationRepository {

    /** DEFAULT VALUES **/
    public static final double DEF_LATITUDE = 0;   // 48.8566;//Paris 48.7258;//VLB 43.0931;//SFLP 48.5959;//SLT
    public static final double DEF_LONGITUDE = 0;  //  2.3522;//Paris  2.1252;//VLB  5.8392;//SFLP  2.5810;//SLT
    public static final int DEFAULT_ZOOM = 17;
    public static final int RESTAURANT_ZOOM = 19;
    /********************/

    private static volatile LocationRepository instance;
    private final MutableLiveData<LatLng> currentLocationMutableLiveData;
    private boolean locationPermissionsGranted;
    private LatLng home;
    private boolean focusHome;

    public LocationRepository() {
        currentLocationMutableLiveData = new MutableLiveData<>();

        focusHome = false;
    }

    public static LocationRepository getInstance() {
        LocationRepository result = instance;
        if (result != null) {
            return result;
        } else {
            instance = new LocationRepository();
            return instance;
        }
    }


    @SuppressWarnings("MissingPermission")  // Permissions already checked in AuthActivity
    public void fetchCurrentLocation(Context context) {
        // Get current location from API
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            // Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(context.getMainExecutor(), task -> {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        // Get the last known location...
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        // Populate the LiveData
                        currentLocationMutableLiveData.setValue(home);
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
                                        // Populate the LiveData
                                        currentLocationMutableLiveData.setValue(home);
                                    }
                                }
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
                else {
                    Log.w("LocationRepository", "Exception: %s", task.getException());
                }
            });

        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }
    }

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return currentLocationMutableLiveData;
    }

    public void setPermissions(boolean granted) {
        locationPermissionsGranted = granted;
    }

    public boolean arePermissionsGranted() {
        return locationPermissionsGranted;
    }

    public LatLng getDefaultLocation() {
        return new LatLng(DEF_LATITUDE, DEF_LONGITUDE);
    }

    public LatLng getCurrentLocation() {
        return home;
    }

    public boolean getFocusHome() {
        return focusHome;
    }

    public void setFocusHome(boolean focus) {
        focusHome = focus;
    }

    public int getDefaultZoom() {
        return DEFAULT_ZOOM;
    }

    public int getRestaurantZoom() {
        return RESTAURANT_ZOOM;
    }

}
