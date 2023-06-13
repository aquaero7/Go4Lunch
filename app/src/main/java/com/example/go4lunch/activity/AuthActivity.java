package com.example.go4lunch.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityAuthBinding;
import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private final UserManager userManager = UserManager.getInstance();
    private ProgressBar progressBar;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    // private FusedLocationProviderClient fusedLocationProviderClient; // TODO : Test MVVM
    boolean locationPermissionsGranted;
    // private LatLng home; // TODO : Test MVVM

    @Override
    ActivityAuthBinding getViewBinding() {
        return ActivityAuthBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind progress bar
        progressBar = binding.progressBarAuth.progressBar;
        // Listen to the clicks on button(s)
        setupListeners();

        // Create a new FusedLocationProviderClient.
        // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // TODO : Test MVVM

        // Check permissions and get device location
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    /*  // TODO : Test MVVM
    @SuppressWarnings("MissingPermission")  // Permissions already checked in the onCreate method
    public void getDataFromApi() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            // Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        // Get the current location...
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        Snackbar.make(binding.getRoot(), getString(R.string.info_location_provided), Snackbar.LENGTH_LONG).show();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        // Initialize home in MapsApisUtils to make it available for ListViewFragment //
                        MapsApisUtils.setHome(home);
                        // Get nearby restaurants list from API to initialize database //
                        List<Restaurant> nearbyRestaurantsList = MapsApisUtils
                                .getRestaurantsFromApi(this, home, getString(R.string.MAPS_API_KEY));
                    } else {
                        getUpdatedDataFromApi();
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

    @SuppressWarnings("MissingPermission")  // Permissions already checked in the onCreate method
    private void getUpdatedDataFromApi() {
        // Setup parameters of location request
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build();
        // Create callback to handle location result
        Activity activity = this;
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
                        Snackbar.make(binding.getRoot(), getString(R.string.info_location_updated), Snackbar.LENGTH_LONG).show();
                        // Initialize home
                        home = new LatLng(latitude, longitude);
                        // Initialize home in MapsApisUtils to make it available for ListViewFragment //
                        MapsApisUtils.setHome(home);
                        // Get nearby restaurants list from API to initialize database //
                        List<Restaurant> nearbyRestaurantsList = MapsApisUtils
                                .getRestaurantsFromApi(getApplicationContext(), home, getString(R.string.MAPS_API_KEY));
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    */

    private void checkPermissions() {
        // This is the result of the user answer to permissions request
        ActivityResultContracts.RequestMultiplePermissions permissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        requestPermissionsLauncher = registerForActivityResult(permissionsContract, result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
            if (fineLocationGranted != null && fineLocationGranted) {
                /** Fine location permission granted */
                Log.w("ActivityResultLauncher", "Fine location permission was granted by user");
                locationPermissionsGranted = true;
                // getDataFromApi();    // TODO : Test MVVM
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                /** Coarse location permission granted */
                Log.w("ActivityResultLauncher", "Only coarse location permission was granted by user");
                locationPermissionsGranted = true;
                // getDataFromApi();    // TODO : Test MVVM
            } else {
                /** No location permission granted */
                Log.w("ActivityResultLauncher", "No location permission was granted by user");
                locationPermissionsGranted = false;
            }
            /** Initialize permission object in MapsApisUtils to make it available for MapViewfragment */
            MapsApisUtils.setPermissions(locationPermissionsGranted);
        });

        // Check and request permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            /** Permissions not granted
             *  Request permissions to user.
             *  The registered ActivityResultCallback gets the result of this(these) request(s). */
            Log.w("checkPermissions", "Permissions not granted");
            requestPermissionsLauncher.launch(PERMISSIONS);
        } else {
            /** Permissions granted */
            Log.w("checkPermissions", "Permissions granted");
            locationPermissionsGranted = true;
            // getDataFromApi();    // TODO : Test MVVM
            /** Initialize permissions in Utils to make them available for fragments */
            MapsApisUtils.setPermissions(locationPermissionsGranted);
        }
    }


    private void setupListeners(){
        // Login Button
        binding.buttonLogin.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
                // Update Firestore utils then launch application
                updateUtilsAndStartApp();
            }else{
                startSignInActivity();
            }
        });
    }

    // Receive and handle response of sign in activity (receiver)
    ActivityResultLauncher<Intent> authActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleResponseAfterSignIn
    );

    // Receive and handle response of main activity
    ActivityResultLauncher<Intent> mainActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleSnackBarAfterLogout
    );

    private void startSignInActivity() {

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

        // Launch the activity
        Intent data = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AuthTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_go4lunch)
                .setAuthMethodPickerLayout(customLayout)    // custom sign in layout
                .build();
        authActivityResultLauncher.launch(data);

        // Show progressBar
        progressBar.setVisibility(View.VISIBLE);

    }

    // Method that handles response after sign in activity close
    private void handleResponseAfterSignIn(ActivityResult result){
        // Show progressBar
        progressBar.setVisibility(View.VISIBLE);

        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
        // SUCCESS
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.info_connection_succeed));
            // Create user in Firestore
            createUser();
        } else {
            // ERRORS
            if (response == null) {
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown));
                }
            }

            // Hide progressBar
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Method that handles response after main activity close
    private void handleSnackBarAfterLogout(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.info_disconnection_succeed));
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void startApp(){
        // Show progressBar
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this,MainActivity.class);
        // startActivity(intent);
        mainActivityResultLauncher.launch(intent);

        // Hide progressBar
        progressBar.setVisibility(View.INVISIBLE);
    }

    // Update Login Button when activity is resuming
    private void updateLoginButton(){
        binding.buttonLogin.setText(userManager.isCurrentUserLogged() ? getString(R.string.button_start) : getString(R.string.button_login));
    }

    private void createUser() {
        FirebaseUser cUser = userManager.getCurrentUser();
        if(cUser != null){
            // Data from FirebaseAuth
            final String USER_ID = "uid";
            String userUrlPicture = (cUser.getPhotoUrl() != null) ? cUser.getPhotoUrl().toString() : null;
            final String USER_NAME = "username";
            String username = cUser.getDisplayName();
            final String USER_EMAIL = "userEmail";
            String userEmail = cUser.getEmail();
            final String USER_URL_PICTURE = "userUrlPicture";
            String uid = cUser.getUid();

            // If the current user already exist in Firestore, we get his data from Firestore
            userManager.getCurrentUserData().addOnSuccessListener(user -> {
                if (user != null) {
                    // If the current user already exist in Firestore, we update his data
                    Log.w("AuthActivity", "User already exists and will be updated");
                    UserManager.getUsersCollection().document(uid)
                            .update(USER_ID, uid, USER_NAME, username, USER_EMAIL, userEmail,
                                    USER_URL_PICTURE, userUrlPicture)
                            .addOnSuccessListener(command -> {
                                Log.w("AuthActivity","Update successful");
                                // Update Firestore utils then launch application
                                updateUtilsAndStartApp();
                            })
                            .addOnFailureListener(e -> Log.w("AuthActivity",
                                    "Update failed. Message : " + e.getMessage()));
                } else {
                    // If the current user doesn't exist in Firestore, we create this user
                    Log.w("AuthActivity", "User doesn't exist and will be created");
                    User userToCreate = new User(uid, username, userEmail, userUrlPicture);
                    UserManager.getUsersCollection().document(uid)
                            .set(userToCreate)
                            .addOnSuccessListener(command -> {
                                Log.w("AuthActivity","Creation successful");
                                // Update Firestore utils then launch application
                                updateUtilsAndStartApp();
                            })
                            .addOnFailureListener(e -> Log.w("AuthActivity",
                                    "Creation failed. Message : " + e.getMessage()));
                }
            });
        }
    }

    private void updateUtilsAndStartApp() {
        // Call step 1/4: Update current user in FirestoreUtils
        updateCurrentUserInFirestoreUtils();
    }

    private void updateCurrentUserInFirestoreUtils() {
        FirestoreUtils.setCurrentUserLogStatus(userManager.isCurrentUserLogged());

        // Get current user from database document
        userManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    String uId = user.getUid();
                    String uName = user.getUsername();
                    String uEmail = user.getUserEmail();
                    String uUrlPicture = user.getUserUrlPicture();
                    String selectionId = user.getSelectionId();
                    String selectionDate = user.getSelectionDate();
                    String searchRadiusPrefs = user.getSearchRadiusPrefs();
                    String notificationsPrefs = user.getNotificationsPrefs();

                    // Update currentUser in FirestoreUtils
                    FirestoreUtils.setCurrentUser(new User(uId, uName, uEmail, uUrlPicture, selectionId,
                            selectionDate, searchRadiusPrefs, notificationsPrefs));
                    // Call step 2/4: Update current user in FirestoreUtils
                    updateRestaurantsListInFirestoreUtils();
                })
                .addOnFailureListener(e -> {
                    Log.w("AuthActivity", e.getMessage());
                });
    }

    private void updateRestaurantsListInFirestoreUtils() {
        // Get restaurants list from database document
        RestaurantManager.getRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get restaurants list
                    List<Restaurant> restaurantsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> restaurantData = document.getData(); // Map data for debug.
                        Restaurant restaurantToAdd = FirestoreUtils.getRestaurantFromDatabaseDocument(document);
                        restaurantsList.add(restaurantToAdd);
                    }

                    // Update restaurantsList in FirestoreUtils
                    FirestoreUtils.setRestaurantsList(restaurantsList);
                    // Call step 3/4: Update workmates list in FirestoreUtils
                    updateWorkmatesListInFirestoreUtils();
                }
            } else {
                Log.d("AuthActivity", "Error getting documents: ", task.getException());
            }
        });
    }

    private void updateWorkmatesListInFirestoreUtils() {
        // Get workmates list from database document
        UserManager.getUsersList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get users list
                    List<User> workmatesList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData = document.getData(); // Map data for debug.
                        User workmateToAdd = FirestoreUtils.getUserFromDatabaseDocument(document);
                        workmatesList.add(workmateToAdd);
                    }

                    // Update workmatesList in FirestoreUtils
                    FirestoreUtils.setWorkmatesList(workmatesList);
                    // Call step 4/4: Update liked restaurants list in FirestoreUtils
                    updateLikedRestaurantsListInFirestoreUtils();
                }
            } else {
                Log.w("FirestoreUtils", "Error getting documents: ", task.getException());
            }
        });
    }

    private void updateLikedRestaurantsListInFirestoreUtils() {
        // Get liked restaurants list from database document
        LikedRestaurantManager.getLikedRestaurantsList(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    // Get liked restaurants list
                    List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> likedRestaurantData = document.getData(); // Map data for debug.
                        LikedRestaurant likedRestaurantToAdd = FirestoreUtils.getLikedRestaurantFromDatabaseDocument(document);
                        likedRestaurantsList.add(likedRestaurantToAdd);
                    }

                    // Update likedRestaurantsList in FirestoreUtils
                    FirestoreUtils.setLikedRestaurantsList(likedRestaurantsList);
                    // Start application
                    startApp();
                }
            } else {
                Log.d("FirestoreUtils", "Error getting documents: ", task.getException());
            }
        });
    }

}