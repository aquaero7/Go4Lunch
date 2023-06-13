package com.example.go4lunch.viewmodel;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;


public class LocationViewModel extends ViewModel {

    private MutableLiveData<LatLng> mMutableLiveData;
    private LatLng home;

    public LocationViewModel() {
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<LatLng> getMutableLiveData() {
        return mMutableLiveData;
    }

    @SuppressWarnings("MissingPermission")  // Permissions already checked in AuthActivity
    public void fetchLocation(Activity activity) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        // Get restaurants list
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
                        // Initialize home in MapsApisUtils to make it available for ListViewFragment //
                        // MapsApisUtils.setHome(home);
                        // Populate the LiveData
                        mMutableLiveData.setValue(home);
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
                                        // Initialize home in MapsApisUtils to make it available for ListViewFragment //
                                        // MapsApisUtils.setHome(home);
                                        // Populate the LiveData
                                        mMutableLiveData.setValue(home);
                                    }
                                }
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
                else {
                    Log.w("getDeviceLocation", "Exception: %s", task.getException());
                }
            });

        } catch (SecurityException e) {
            Log.w("Exception: %s", e.getMessage(), e);
        }
    }
}
