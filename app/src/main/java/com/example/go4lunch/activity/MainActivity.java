package com.example.go4lunch.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.fragment.ListViewFragment;
import com.example.go4lunch.fragment.MapViewFragment;
import com.example.go4lunch.fragment.PagerAdapter;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.viewmodel.LikedRestaurantViewModel;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, EventListener {

    // For Navigation Drawer design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // For ViewPager and Tabs
    private ViewPager2 pager;
    private TabLayout tabs;
    private String[] tabTitles;

    private ImageView userPicture;
    private TextView userName;
    private TextView userEmail;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    String message;
    private Fragment fmt;
    private SearchView searchView;
    private final String currentDate = CalendarUtils.getCurrentDate();
    private String uid;
    private final UserManager userManager = UserManager.getInstance();
    private final RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private final LikedRestaurantManager likedRestaurantManager = LikedRestaurantManager.getInstance();
    private LocationViewModel locationViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;
    private LikedRestaurantViewModel likedRestaurantViewModel;
    private List<User> workmatesList = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
    private User currentUser;
    private boolean selectionIsNearBy;


    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the toolbar view
        this.configureToolbar();
        // Configure ViewPager and tabs
        this.configureViewPagerAndTabs();
        // Configure Navigation Drawer views
        this.configureDrawerLayout();
        this.configureNavigationView();
        // Configure progressBar
        this.configureProgressBar();
        // Initialize SearchView and setup listener
        searchView = binding.includedToolbar.searchView;
        this.configureSearchViewListener(searchView);
        // Initialize ViewModels and fetch data
        this.initViewModelsAndData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update data
        this.updateData();
    }

    @Override
    public void toggleSearchViewVisibility() {
        SearchView view = binding.includedToolbar.searchView;
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void initViewModelsAndData() {
        // Initialize location ViewModel
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        /** Get data from MAPS API */
        locationViewModel.fetchLocation(this);
        // Initialize restaurant ViewModel
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        /** Get data from MAPS API */
        restaurantViewModel.fetchRestaurants(this, getString(R.string.MAPS_API_KEY));
        // Initialize user ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        /** Get data from Firebase */
        userViewModel.fetchWorkmates();
        // Initialize liked restaurant ViewModel
        likedRestaurantViewModel = new ViewModelProvider(this).get(LikedRestaurantViewModel.class);
        /** Get data from Firebase */
        likedRestaurantViewModel.fetchLikedRestaurants();
    }

    private void updateData() {
        // Update workmates list
        userViewModel.fetchWorkmates();
        // Update liked restaurants list
        likedRestaurantViewModel.fetchLikedRestaurants();

        // Initialize workmates data
        userViewModel.getMutableLiveData().observe(this, workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
        });
        // Initialize liked restaurants data
        likedRestaurantViewModel.getMutableLiveData().observe(this, likedRestaurants -> {
            likedRestaurantsList.clear();
            likedRestaurantsList.addAll(likedRestaurants);
        });
    }

    /**
     * ---------------------
     * LISTENERS
     * ---------------------
     */

    @Override
    public void onBackPressed() {
        // Handle Navigation Drawer back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle Navigation Drawer Item Click
        int id = item.getItemId();
        switch (id){
            case R.id.activity_main_drawer_lunch:
                checkCurrentUserSelectionAndLaunchActivity();
                break;
            case R.id.activity_main_drawer_settings:
                launchSettingActivity();
                break;
            case R.id.activity_main_drawer_logout:
                logout();
                break;
            case R.id.activity_main_drawer_delete_account:
                dialog.show();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
    * ---------------------
    * CONFIGURATIONS
    * ---------------------
    */

    private void configureToolbar(){
        // Get the toolbar view inside the activity layout
        this.toolbar = binding.includedToolbar.toolbar;
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    private void configureViewPagerAndTabs(){
        // Get ViewPager from layout
        pager = binding.activityMainViewpager;

        // Set Adapter PagerAdapter and glue it together
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle()));

        // Get TabLayout from layout
        tabs = binding.activityMainTabs;

        // Initialize tab titles list
        final String MAP_VIEW_TAB_TITLE = getString(R.string.tab_map_view);
        final String LIST_VIEW_TAB_TITLE = getString(R.string.tab_list_view);
        final String WORKMATES_TAB_TITLE = getString(R.string.tab_workmates);
        tabTitles= new String[]{MAP_VIEW_TAB_TITLE, LIST_VIEW_TAB_TITLE, WORKMATES_TAB_TITLE};

        // Initialize tab icons list
        final int[] tabIcons = {
                R.drawable.ic_baseline_map_black_24,
                R.drawable.ic_baseline_view_list_black_24,
                R.drawable.ic_baseline_group_black_24
        };

        // Glue TabLayout and ViewPager together
        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            // Setup tab title
            tab.setText(tabTitles[position]);
            // Setup tab icon
            tab.setIcon(tabIcons[position]);
        }).attach();

        // Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);

        // Add a listener to detect tab selection change
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                searchView.setQuery("", false);
                searchView.setVisibility(View.GONE);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = binding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = binding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(this);
        buildConfirmationDialog();

        // Call user information update
        userPicture = navigationView.getHeaderView(0).findViewById(R.id.user_picture);
        userName = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        updateUIWithUserData();
    }

    // Configure progressBar
    private void configureProgressBar() {
        ProgressBar progressBar = binding.activityMainProgressBar.progressBar;
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void configureSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Get fragment's identification
                fmt = getSupportFragmentManager().findFragmentByTag("f" + pager.getCurrentItem());
                switch (fmt.getTag()) {
                    case "f0":
                        ((MapViewFragment)fmt).launchAutocomplete(query);
                        searchView.setQuery("", false);
                        break;
                    case "f1":
                        ((ListViewFragment)fmt).filterList(query);
                        break;
                    case "f2":
                        showSnackBar(String.format(getString(R.string.error_search), tabTitles[pager.getCurrentItem()]));
                        searchView.setQuery("", false);
                        break;
                }
                searchView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Get fragment's identification
                fmt = getSupportFragmentManager().findFragmentByTag("f" + pager.getCurrentItem());
                switch (fmt.getTag()) {
                    case "f0":
                        if (newText.length() == 3) {
                            searchView.setQuery("", false);
                            searchView.setVisibility(View.GONE);
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

        searchView.setOnCloseListener(() -> {
            // Get fragment's identification
            fmt = getSupportFragmentManager().findFragmentByTag("f" + pager.getCurrentItem());
            switch (fmt.getTag()) {
                case "f0":
                case "f2":
                    break;
                case "f1":
                    ((ListViewFragment)fmt).filterList("");
                    break;
            }
            searchView.setVisibility(View.GONE);
            return false;
        });
    }

    /**
     * ---------------------
     * METHODS
     * ---------------------
     */

    private void checkIfSelectionIsNearBy(String id) {
        restaurantViewModel.getMutableLiveData().observe(this, restaurants -> {
            selectionIsNearBy = false;
            for (Restaurant restaurant : restaurants) {
                if (Objects.equals(id, restaurant.getRid())) {
                    selectionIsNearBy = true;
                    break;
                }
            }
        });
    }

    private void checkCurrentUserSelectionAndLaunchActivity() {
        // Get current user from database document
        userManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                    // Get current user selected restaurant
                    String selectionId = currentUser.getSelectionId();
                    String selectionDate = currentUser.getSelectionDate();
                    checkIfSelectionIsNearBy(selectionId);
                    boolean isSelected = selectionId != null && selectionIsNearBy && currentDate.equals(selectionDate);
                    if (isSelected) {
                        // Get selected restaurant from restaurants collection in database
                        restaurantManager.getRestaurantData(selectionId)
                                .addOnSuccessListener(restaurant -> {
                                    Log.w("MainActivity", "success task getRestaurantData");
                                    LatLng home = MapsApisUtils.getHome();
                                    int distance = (home != null) ?
                                            DataProcessingUtils.calculateRestaurantDistance(restaurant, home) : 0;

                                    RestaurantWithDistance restaurantWithDistance =
                                            new RestaurantWithDistance(restaurant.getRid(), restaurant.getName(),
                                                    restaurant.getPhotos(), restaurant.getAddress(),
                                                    restaurant.getRating(), restaurant.getOpeningHours(),
                                                    restaurant.getPhoneNumber(), restaurant.getWebsite(),
                                                    restaurant.getGeometry(), distance);

                                    launchDetailRestaurantActivity(restaurantWithDistance);
                                })
                                .addOnFailureListener(e -> Log.w("MainActivity", e.getMessage()));
                    } else {
                        showSnackBar(getString(R.string.error_choice));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("UserViewModel", e.getMessage());
                });
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurant) {
        Intent intent = new Intent(MainActivity.this, DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        bundle.putSerializable("WORKMATES", (Serializable) workmatesList);
        bundle.putSerializable("LIKED_RESTAURANTS", (Serializable) likedRestaurantsList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void launchSettingActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // Update user information (using User model and FirestoreUtils instead of FirebaseUser model and UserManager)
    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();
            if(user.getPhotoUrl() != null){
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    // Update user picture
    private void setProfilePicture(Uri profilePictureUrl){
        userPicture.setImageTintList(null);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userPicture);
    }
    // Update user name and email
    private void setTextUserData(FirebaseUser user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();
        //Update views with data
        userName.setText(username);
        userEmail.setText(email);
    }

    private void buildConfirmationDialog() {
        // Create the builder with a specific theme setting (i.e. for all buttons text color)...
        // builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        // ...or create the builder with no specific theme setting
        builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    message = getString(R.string.dialog_info_ok);
                    showSnackBar(message);
                    deleteAccountAndLogout();
                })
                .setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> {
                    message = getString(R.string.dialog_info_cancel);
                    showSnackBar(message);
                })
        // Chain together various setter methods to set the dialog characteristics
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message);
        // Create the AlertDialog
        dialog = builder.create();
        // Set color for each button text (so, no need to set theme when building AlertDialog)
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.green_fab));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.red));
        });
    }

    private void logout(){
        userManager.signOut(this).addOnSuccessListener(aVoid -> closeActivity());
    }

    private void deleteAccountAndLogout() {
        uid = userManager.getCurrentUserId();
        // Clean up liked restaurants list
        likedRestaurantViewModel.getMutableLiveData().observe(this, likedRestaurants -> {
            // Delete current user's likes from Firestore liked restaurants collection
            for (LikedRestaurant likedRestaurant : likedRestaurants) {
                if (likedRestaurant.getUid().equals(uid))
                    likedRestaurantManager.deleteLikedRestaurant(likedRestaurant.getId());
            }
        });
        // Delete current user from Firestore users collection
        userManager.deleteUser(uid);
        // Delete current user from Firebase and logout
        userManager.deleteFirebaseUser(MainActivity.this)
                .addOnSuccessListener(aVoid -> {
                    message = getString(R.string.dialog_info_deletion_confirmation);
                    showSnackBar(message);
                    logout();
                });
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}