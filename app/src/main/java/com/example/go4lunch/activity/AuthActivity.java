package com.example.go4lunch.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityAuthBinding;
import com.example.go4lunch.manager.UserManager;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;


public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private final UserManager userManager = UserManager.getInstance();
    private ProgressBar progressBar;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    private void setupListeners(){
        // Login Button
        binding.buttonLogin.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
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
            showSnackBar(getString(R.string.connection_succeed));
            // Create user into Firestore
            userManager.createUser();
            startApp();
        } else {
            // ERRORS
            if (response == null) {
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }

            // Hide progressBar
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Method that handles response after main activity close
    private void handleSnackBarAfterLogout(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.disconnection_succeed));
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        // Snackbar.make(binding.authLayout, message, Snackbar.LENGTH_LONG).show();
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        binding.buttonLogin.setText(userManager.isCurrentUserLogged() ? getString(R.string.start_button) : getString(R.string.login_button));
    }

}