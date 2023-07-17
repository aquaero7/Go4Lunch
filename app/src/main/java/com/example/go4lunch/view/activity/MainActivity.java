package com.example.go4lunch.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.model.model.DialogTuple;
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.view.fragment.ListViewFragment;
import com.example.go4lunch.view.fragment.MapViewFragment;
import com.example.go4lunch.view.fragment.PagerAdapter;
import com.example.go4lunch.R;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, EventListener {

    private MainViewModel mainViewModel;
    private AlertDialog dialog;
    private String signInProvider;

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mainViewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication())).get(MainViewModel.class); // If VM extends AndroidViewModel
        mainViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(MainViewModel.class); // If VM extends ViewModel
        configureToolbar();
        configureViewPagerAndTabs();
        configureDrawerLayout();
        configureNavigationView();
        configureSearchViewListener();
        configureReAuthListener();
        // Initialize current user
        mainViewModel.getCurrentUserMutableLiveData();
    }

    /**
     * ---------------------
     * LISTENERS
     * ---------------------
     */

    @Override
    public void onBackPressed() {
        // Handle Navigation Drawer back click to close menu
        if (binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle Navigation Drawer Item Click
        switch (item.getItemId()){
            case R.id.activity_main_drawer_lunch:
                launchDetailRestaurantActivity();
                break;
            case R.id.activity_main_drawer_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.activity_main_drawer_logout:
                logoutAndCloseActivity();
                break;
            case R.id.activity_main_drawer_delete_account:
                dialog.show();
                break;
            default:
                break;
        }
        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        // Manage click on button to valid password for reAuth
        if (v.getId() == R.id.bt_pwd_send && !Objects.requireNonNull(binding.etPwd.getText()).toString().isEmpty()) {
            Utils.getInstance().hideVirtualKeyboard(this, v);
            launchReAuth(signInProvider);
        }
    }

    @Override
    public void toggleSearchViewVisibility() {
        SearchView view = binding.includedToolbar.searchView;
        view.setVisibility((view.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    /**
    * ---------------------
    * CONFIGURATIONS
    * ---------------------
    */

    private void configureToolbar(){
        // Sets the Toolbar
        setSupportActionBar(binding.includedToolbar.toolbar);
    }

    private void configureViewPagerAndTabs(){
        // Set Adapter PagerAdapter and glue it together
        binding.activityMainViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle()));

        // Glue TabLayout and ViewPager together
        new TabLayoutMediator(binding.activityMainTabs, binding.activityMainViewpager, (tab, position) -> {
            // Setup tab title
            tab.setText(mainViewModel.getTabTitles()[position]);
            // Setup tab icon
            tab.setIcon(mainViewModel.getTabIcons()[position]);
        }).attach();

        // Design purpose. Tabs have the same width
        binding.activityMainTabs.setTabMode(TabLayout.MODE_FIXED);

        // Add a listener to detect tab selection change
        binding.activityMainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Show message if location permissions are denied
                if (!mainViewModel.arePermissionsGranted()) showSnackBar(getString(R.string.info_no_permission));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                binding.includedToolbar.searchView.setQuery("", false);
                binding.includedToolbar.searchView.setVisibility(View.GONE);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Show message if location permissions are denied
                if (!mainViewModel.arePermissionsGranted()) showSnackBar(getString(R.string.info_no_permission));
            }
        });
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.activityMainDrawerLayout,
                binding.includedToolbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.activityMainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        binding.activityMainNavView.setNavigationItemSelectedListener(this);
        // Create confirmation dialog
        buildConfirmationDialog();
        // Update nav view with user information
        updateUIWithUserData();
    }

    private void configureSearchViewListener() {
        binding.includedToolbar.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Get fragment's identification
                Fragment fmt = getSupportFragmentManager().findFragmentByTag("f" + binding.activityMainViewpager.getCurrentItem());
                switch (Objects.requireNonNull(Objects.requireNonNull(fmt).getTag())) {
                    case "f0":
                        ((MapViewFragment)fmt).launchAutocomplete(query);
                        binding.includedToolbar.searchView.setQuery("", false);
                        break;
                    case "f1":
                        ((ListViewFragment)fmt).filterList(query);
                        break;
                    case "f2":
                        showSnackBar(String.format(getString(R.string.error_search), mainViewModel.getTabTitles()[binding.activityMainViewpager.getCurrentItem()]));
                        binding.includedToolbar.searchView.setQuery("", false);
                        break;
                }
                binding.includedToolbar.searchView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Get fragment's identification
                Fragment fmt = getSupportFragmentManager().findFragmentByTag("f" + binding.activityMainViewpager.getCurrentItem());
                switch (Objects.requireNonNull(Objects.requireNonNull(fmt).getTag())) {
                    case "f0":
                        // Don't trigger predictions before 3 chars input
                        if (newText.length() == 3) {
                            binding.includedToolbar.searchView.setQuery("", false);
                            binding.includedToolbar.searchView.setVisibility(View.GONE);
                            ((MapViewFragment)fmt).launchAutocomplete(newText);
                        }
                        break;
                    case "f1":
                        ((ListViewFragment)fmt).filterList(newText);
                        break;
                    case "f2":
                        break;
                }
                return false;
            }


        });

        binding.includedToolbar.searchView.setOnCloseListener(() -> {
            // Get fragment's identification
            Fragment fmt = getSupportFragmentManager().findFragmentByTag("f" + binding.activityMainViewpager.getCurrentItem());
            switch (Objects.requireNonNull(Objects.requireNonNull(fmt).getTag())) {
                case "f0":
                case "f2":
                    break;
                case "f1":
                    ((ListViewFragment)fmt).filterList("");
                    break;
            }
            binding.includedToolbar.searchView.setVisibility(View.GONE);
            return false;
        });
    }

    private void configureReAuthListener() {
        // binding.btPwdVisibility.setOnClickListener(this);
        binding.btPwdSend.setOnClickListener(this);
    }

    /**
     * ---------------------
     * METHODS
     * ---------------------
     */

    private void launchDetailRestaurantActivity() {
        RestaurantWithDistance restaurant = mainViewModel.getCurrentUserSelection();
        // Launch DetailActivity or show message
        if (restaurant != null) {
            Intent intent = new Intent(MainActivity.this, DetailRestaurantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RESTAURANT", restaurant);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            showSnackBar(getString(R.string.error_choice));
        }
    }

    // Update Firebase user information
    private void updateUIWithUserData(){
        if(mainViewModel.isFbCurrentUserLogged()){
            FirebaseUser fbUser = mainViewModel.getFbCurrentUser();
            // Set and display profile picture
            if(fbUser.getPhotoUrl() != null){
                ImageView userPicture = binding.activityMainNavView.getHeaderView(0).findViewById(R.id.user_picture);
                userPicture.setImageTintList(null);
                Glide.with(this)
                        .load(fbUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPicture);
            }
            // Set and display name
            String name = TextUtils.isEmpty(fbUser.getDisplayName()) ? getString(R.string.info_no_username_found) : fbUser.getDisplayName();
            TextView userName = binding.activityMainNavView.getHeaderView(0).findViewById(R.id.user_name);
            userName.setText(name);
            // Set and display email
            String email = TextUtils.isEmpty(fbUser.getEmail()) ? getString(R.string.info_no_email_found) : fbUser.getEmail();
            TextView userEmail = binding.activityMainNavView.getHeaderView(0).findViewById(R.id.user_email);
            userEmail.setText(email);
        }
    }

    // Build dialog for account deletion
    private void buildConfirmationDialog() {
        // Create the builder with a specific theme setting (i.e. for all buttons text color)...
        // builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        // ...or create the builder with no specific theme setting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Configure builder and create dialog
        DialogTuple<AlertDialog, MutableLiveData<Boolean>> dialogTuple = mainViewModel.buildConfirmationDialog(builder);
        // Get dialog
        dialog = dialogTuple.getDialog();
        // Manage response
        dialogTuple.getResponse().observe(this, response -> {
            if (response != null) {
                if (response) {
                    showSnackBar(getString(R.string.dialog_info_ok));
                    deleteAccountLogoutAndCloseActivity();
                } else {
                    showSnackBar(getString(R.string.dialog_info_cancel));
                }
            }
        });
    }

    private void logoutAndCloseActivity(){
        mainViewModel.signOut()
                .addOnSuccessListener(aVoid -> {
                    Log.w("MainActivity", "Logout successful");
                    setResult(RESULT_OK, new Intent());
                    finish();
                })
                .addOnFailureListener(e -> Log.w("MainActivity", e.getMessage()));
    }

    private void deleteAccountLogoutAndCloseActivity() {
        mainViewModel.deleteUserLikesAndUser();
        mainViewModel.deleteFbUser()
                .addOnSuccessListener(aVoid -> {
                    showSnackBar(getString(R.string.dialog_info_deletion_confirmation));
                    logoutAndCloseActivity();
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", e.getMessage());
                    FirebaseAuth.getInstance().getAccessToken(false).addOnSuccessListener((OnSuccessListener<GetTokenResult>) getTokenResult -> {
                                signInProvider = getTokenResult.getSignInProvider();
                                if (Objects.equals("password", signInProvider)) {
                                    showSnackBar(getString(R.string.reauth_message));
                                    showReAutView();
                                } else {
                                    showSnackBar(getString(R.string.relog_message) +"\n"+ getString(R.string.relog_message));
                                }
                    });
                });
    }

    private void showReAutView() {
        binding.activityMainDrawerLayout.openDrawer(GravityCompat.START);
        binding.tvPwd.setVisibility(View.VISIBLE);
        binding.lytPwd.setVisibility(View.VISIBLE);
    }

    private void launchReAuth(String signInProvider) {
        String passWord = String.valueOf(binding.etPwd.getText());
        mainViewModel.getFbCurrentUser().reauthenticate(Objects.requireNonNull(mainViewModel.getCredential(signInProvider, passWord)))
                .addOnSuccessListener(unused -> {
                    Objects.requireNonNull(binding.etPwd.getText()).clear();
                    binding.tvPwd.setVisibility(View.INVISIBLE);
                    binding.lytPwd.setVisibility(View.INVISIBLE);
                    showSnackBar("Re-authentication succeed");
                    deleteAccountLogoutAndCloseActivity();
                })
                .addOnFailureListener(e -> {
                    binding.tvPwd.setText(getString(R.string.pwd_error));
                    showSnackBar("Re-authentication failed");
                });
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}