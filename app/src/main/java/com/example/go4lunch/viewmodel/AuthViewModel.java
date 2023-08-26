package com.example.go4lunch.viewmodel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AuthViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;

    // Constructor
    public AuthViewModel(
            UserRepository userRepository, LocationRepository locationRepository,
            RestaurantRepository restaurantRepository, LikedRestaurantRepository likedRestaurantRepository) {

        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.restaurantRepository = restaurantRepository;
        this.likedRestaurantRepository = likedRestaurantRepository;
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return locationRepository.getCurrentLocationMutableLiveData();
    }

    public MutableLiveData<Boolean> getUserCreationResponseMutableLiveData() {
        return userRepository.getUserCreationResponseMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Fetchers (using Maps and Firebase APIs)

    public void fetchDataExceptRestaurants(Context context) {
        fetchCurrentUser();
        fetchCurrentLocation(context);
        fetchWorkmates();
        fetchLikedRestaurants();
    }

    public void fetchCurrentUser() {
        userRepository.fetchCurrentUser();
    }

    public void fetchWorkmates() {
        userRepository.fetchWorkmates();
    }

    public void fetchCurrentLocation(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationRepository.fetchCurrentLocation(fusedLocationProviderClient, context);
    }

    public void fetchRestaurants(LatLng home, String apiKey) {
        if (userRepository.getCurrentUserData() != null) {
            userRepository.getCurrentUserData()
                    .addOnSuccessListener(user -> {
                        if (user != null) {
                            restaurantRepository.fetchRestaurants(home, getSearchRadius(user), apiKey);
                            Log.w("AuthViewModel", "user radius: " + getSearchRadius(user));
                        } else {
                            Log.w("AuthViewModel", "user is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("AuthViewModel", Objects.requireNonNull(e.getMessage()));
                    });
        } else {
            Log.w("AuthViewModel", "user is null");
        }
    }

    public void fetchLikedRestaurants() {
        likedRestaurantRepository.fetchLikedRestaurants();
    }


    // Actions

    public void createUser() {
        userRepository.createUser();
    }

    public void manageUserAnswerForPermissions(Map<String, Boolean> result) {
        // This is the result of the user answer to permissions request
        // Permissions could be : Fine location / Coarse location / No location
        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
        if (fineLocationGranted != null && fineLocationGranted) {
            /** Fine location permission granted */
            Log.w("ActivityResultLauncher", "Fine location permission was granted by user");
            locationRepository.setPermissions(true);
        } else if (coarseLocationGranted != null && coarseLocationGranted) {
            /** Coarse location permission granted */
            Log.w("ActivityResultLauncher", "Only coarse location permission was granted by user");
            locationRepository.setPermissions(true);
        } else {
            /** No location permission granted */
            Log.w("ActivityResultLauncher", "No location permission was granted by user");
            locationRepository.setPermissions(false);
        }
    }

    public void checkAndRequestPermissions(ActivityResultLauncher<String[]> requestPermissionsLauncher, Context context) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            /** Permissions not granted
             *  Request permissions to user.
             *  The registered ActivityResultCallback gets the result of this(these) request(s). */
            Log.w("checkPermissions", "Permissions not granted");
            final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissionsLauncher.launch(PERMISSIONS);
        } else {
            /** Permissions granted */
            Log.w("checkPermissions", "Permissions granted");
            locationRepository.setPermissions(true);
        }
    }

    public Intent configureAuthIntent() {

        // Custom sign in layout instead of default one
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.layout_sign_in)
                .setGoogleButtonId(R.id.button_google)
                .setFacebookButtonId(R.id.button_facebook)
                .setTwitterButtonId(R.id.button_twitter)
                .setEmailButtonId(R.id.button_email)
                .build();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build());

        // Create intent
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AuthTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_go4lunch)
                .setAuthMethodPickerLayout(customLayout)    // custom sign in layout
                .build();

        return intent;
    }


    // Getters

    public boolean isFbCurrentUserLogged() {
        return userRepository.isFbCurrentUserLogged();
    }

    public String getSearchRadius(User user) {
        String searchRadius = user.getSearchRadiusPrefs();
        return (searchRadius != null) ? searchRadius : restaurantRepository.getDefaultRadius();
    }

}
