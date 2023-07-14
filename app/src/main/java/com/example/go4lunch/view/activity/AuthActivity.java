package com.example.go4lunch.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityAuthBinding;
import com.example.go4lunch.viewmodel.AuthViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;


public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private AuthViewModel authViewModel;

    @Override
    ActivityAuthBinding getViewBinding() {
        return ActivityAuthBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        // authViewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication())).get(AuthViewModel.class); // If VM extends AndroidViewModel
        authViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(AuthViewModel.class); // If VM extends ViewModel
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
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(permissionsContract, result -> {
            authViewModel.manageUserAnswerForPermissions(result);
        });

        // Check and request permissions
        authViewModel.checkAndRequestPermissions(requestPermissionsLauncher);
    }

    private void setupListeners(){
        // Login Button
        binding.buttonLogin.setOnClickListener(view -> {
            if(authViewModel.isFbCurrentUserLogged()){
                // Start application
                startApp();
            }else{
                // Start sign in activity
                authActivityResultLauncher.launch(authViewModel.configureAuthIntent());
            }
        });
    }

    // Update Login Button when activity is resuming
    private void updateLoginButton(){
        binding.buttonLogin.setText(authViewModel.isFbCurrentUserLogged() ? getString(R.string.button_start) : getString(R.string.button_login));
    }

    // Receive and handle response of sign in activity (receiver)
    ActivityResultLauncher<Intent> authActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleResponseAfterSignIn
    );

    // Receive and handle response of main activity (receiver)
    ActivityResultLauncher<Intent> mainActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleSnackBarAfterLogout
    );

    // Method that handles response after sign in activity close
    private void handleResponseAfterSignIn(ActivityResult result){
        // Handle response
        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
        // SUCCESS
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.info_connection_succeed));
            // Create user in Firestore
            createUserAndStartApplication();
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
        }
    }

    // Method that handles response after main activity close
    private void handleSnackBarAfterLogout(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.info_disconnection_succeed));
        }
    }

    private void createUserAndStartApplication() {
        authViewModel.createUser();
        authViewModel.getUserCreationResponseMutableLiveData().observe(this, new Observer<Boolean>() {
            final Observer<Boolean> observer = this;
            @Override
            public void onChanged(Boolean response) {
                if (response) {
                    authViewModel.getUserCreationResponseMutableLiveData().removeObserver(observer);
                    startApp();
                }
            }
        });
    }

    private void startApp(){
        // Show progressBar
        binding.progressbarAuth.progressbar.setVisibility(View.VISIBLE);
        // Fetch data
        authViewModel.fetchDataExceptRestaurants();
        authViewModel.getCurrentLocationMutableLiveData().observe(this, home -> {
            // Fetch restaurants
            authViewModel.fetchRestaurants(home, getString(R.string.MAPS_API_KEY));
            // Hide progressBar
            binding.progressbarAuth.progressbar.setVisibility(View.INVISIBLE);
        });
        // Launch main activity
        Intent intent = new Intent(this,MainActivity.class);
        mainActivityResultLauncher.launch(intent);
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}