package com.example.go4lunch.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityAuthBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewmodel.DrawerViewModel;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private final UserManager userManager = UserManager.getInstance();
    private ProgressBar progressBar;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    boolean locationPermissionsGranted;

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
        // Check permissions and get device location
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

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
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                /** Coarse location permission granted */
                Log.w("ActivityResultLauncher", "Only coarse location permission was granted by user");
                locationPermissionsGranted = true;
            } else {
                /** No location permission granted */
                Log.w("ActivityResultLauncher", "No location permission was granted by user");
                locationPermissionsGranted = false;
            }
            /** Initialize permission object in MapsApisUtils to make it available for MapViewfragment */
            userManager.setPermissions(locationPermissionsGranted);
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
            /** Initialize permissions in Utils to make them available for fragments */
            userManager.setPermissions(locationPermissionsGranted);
        }
    }


    private void setupListeners(){
        // Login Button
        binding.buttonLogin.setOnClickListener(view -> {
            if(userManager.isFbCurrentUserLogged()){
                startApp();
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

    // Update Login Button when activity is resuming
    private void updateLoginButton(){
        binding.buttonLogin.setText(userManager.isFbCurrentUserLogged() ? getString(R.string.button_start) : getString(R.string.button_login));
    }

    private void createUser() {
        FirebaseUser cUser = userManager.getFbCurrentUser();
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
                    userManager.getUsersCollection().document(uid)
                            .update(USER_ID, uid, USER_NAME, username, USER_EMAIL, userEmail,
                                    USER_URL_PICTURE, userUrlPicture)
                            .addOnSuccessListener(command -> {
                                Log.w("AuthActivity","Update successful");
                                startApp();
                            })
                            .addOnFailureListener(e -> Log.w("AuthActivity",
                                    "Update failed. Message : " + e.getMessage()));
                } else {
                    // If the current user doesn't exist in Firestore, we create this user
                    Log.w("AuthActivity", "User doesn't exist and will be created");
                    User userToCreate = new User(uid, username, userEmail, userUrlPicture);
                    userManager.getUsersCollection().document(uid)
                            .set(userToCreate)
                            .addOnSuccessListener(command -> {
                                Log.w("AuthActivity","Creation successful");
                                startApp();
                            })
                            .addOnFailureListener(e -> Log.w("AuthActivity",
                                    "Creation failed. Message : " + e.getMessage()));
                }
            });
        }
    }

    private void startApp(){

        // Fetch data
        // initData(); // TODO : Test implementation in MainActivity or AuthActivity

        // Show progressBar
        progressBar.setVisibility(View.VISIBLE);
        // Launch main activity
        Intent intent = new Intent(this,MainActivity.class);
        mainActivityResultLauncher.launch(intent);
        // Hide progressBar
        progressBar.setVisibility(View.INVISIBLE);
    }

    // TODO : Test implementation in MainActivity or AuthActivity
    private void initData() {
        DrawerViewModel drawerViewModel = new ViewModelProvider(this).get(DrawerViewModel.class);

        drawerViewModel.fetchWorkmates();

        // drawerViewModel.fetchCurrentLocationAndRestaurants(this, getString(R.string.MAPS_API_KEY));
        Activity activity = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerViewModel.fetchCurrentLocationAndRestaurants(activity, getString(R.string.MAPS_API_KEY));
            }
        }, 10000);

        drawerViewModel.fetchLikedRestaurants();
    }

}