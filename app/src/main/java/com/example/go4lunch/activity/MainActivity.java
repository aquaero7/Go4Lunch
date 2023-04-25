package com.example.go4lunch.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, EventListener {

    // private static Activity mActivity;

    // For Navigation Drawer design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // For ViewPager and Tabs
    private ViewPager2 pager;
    private TabLayout tabs;
    private final String MAP_VIEW_TAB_TITLE = "Map View";
    private final String LIST_VIEW_TAB_TITLE = "List View";
    private final String WORKMATES_TAB_TITLE = "Workmates";
    private final String [] tabTitles={     // Initialize title list
            MAP_VIEW_TAB_TITLE,
            LIST_VIEW_TAB_TITLE,
            WORKMATES_TAB_TITLE};
    int[] tabIcons = {                      // Initialize icon list
            R.drawable.ic_baseline_map_black_24,
            R.drawable.ic_baseline_view_list_black_24,
            R.drawable.ic_baseline_group_black_24
    };

    private ImageView userPicture;
    private TextView userName;
    private TextView userEmail;

    // private String MAPS_API_KEY;
    // private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    // private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    // private static FusedLocationProviderClient fusedLocationProviderClient;
    // private static boolean locationPermissionsGranted;
    // private static LatLng home;
    // private static List<Restaurant> restaurantsList;            // To make it available for fragments in FirestoreUtils
    // private static List<User> workmatesList;                    // To make it available for fragments in FirestoreUtils
    // private static List<LikedRestaurant> likedRestaurantsList;  // To make it available for fragments in FirestoreUtils
    // private User currentUser;                                   // To make it available for fragments in FirestoreUtils

    private Fragment fmt;
    private SearchView searchView;

    private final String currentDate = CalendarUtils.getCurrentDate();
    private String uid;

    private final UserManager userManager = UserManager.getInstance();


    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mActivity = this;
        // MAPS_API_KEY = getString(R.string.MAPS_API_KEY);

        // Get current user Id
        //getCurrentUserId();

        // Get the toolbar view
        this.configureToolbar();

        // Configure ViewPager and tabs
        this.configureViewPagerAndTabs();

        // Configure Navigation Drawer views
        this.configureDrawerLayout();
        this.configureNavigationView();

        // Configure progressBar
        this.configureProgressBar();

        // Create a new FusedLocationProviderClient.
        // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Check permissions and get device location
        // checkPermissions();

        // Initialize SearchView and setup listener
        searchView = binding.includedToolbar.searchView;
        this.configureSearchViewListener(searchView);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("UID", uid);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (uid.isEmpty()) uid = savedInstanceState.getString("UID");
    }
    */

    @Override
    public void toggleSearchViewVisibility() {
        SearchView view = binding.includedToolbar.searchView;
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
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

        // Call user information update
        userPicture = navigationView.getHeaderView(0).findViewById(R.id.user_picture);
        userName = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        updateUIWithUserData();
    }

    // Configure progressBar
    private void configureProgressBar() {
        ProgressBar progressBar = binding.progressBarMain.progressBar;
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
                        showSnackBar(String.format(getString(R.string.search_error), tabTitles[pager.getCurrentItem()]));
                        searchView.setQuery("", false);
                        break;
                }
                searchView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*  // TODO : For debug : To be deleted
                int testId = getSupportFragmentManager().findFragmentById(pager.getCurrentItem()).getId();
                String testTag = getSupportFragmentManager().findFragmentByTag("f" + pager.getCurrentItem()).getTag();
                MapViewFragment fm = (MapViewFragment) getSupportFragmentManager().findFragmentByTag("f" + pager.getCurrentItem());
                */

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

    /*
    private void getCurrentUserId() {
        uid = userManager.getCurrentUserId();
    }

    private void initializeListsInUtils() {
        /** Initialize data objects in FirestoreUtils to make them available for fragments //
        currentUser = FirestoreUtils.getCurrentUserFromDatabaseDocument();
        restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();
        workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
        likedRestaurantsList = FirestoreUtils.getLikedRestaurantsListFromDatabaseDocument();
    }

    private void checkPermissions() {
        // This is the result of the user answer to permissions request
        ActivityResultContracts.RequestMultiplePermissions permissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        requestPermissionsLauncher = registerForActivityResult(permissionsContract, result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
            if (fineLocationGranted != null && fineLocationGranted) {
                /** Fine location permission granted //
                Log.w("ActivityResultLauncher", "Fine location permission was granted by user");
                locationPermissionsGranted = true;
                initializeListsInUtils();
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                /** Coarse location permission granted //
                Log.w("ActivityResultLauncher", "Only coarse location permission was granted by user");
                locationPermissionsGranted = true;
                initializeListsInUtils();
            } else {
                /** No location permission granted //
                Log.w("ActivityResultLauncher", "No location permission was granted by user");
                locationPermissionsGranted = false;
            }
            /** Initialize permission object in MapsApisUtils to make it available for MapViewfragment //
            MapsApisUtils.setPermissions(locationPermissionsGranted);
        });

        // Check and request permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            /** Permissions not granted
             *  Request permissions to user.
             *  The registered ActivityResultCallback gets the result of this(these) request(s). //
            Log.w("checkPermissions", "Permissions not granted");
            requestPermissionsLauncher.launch(PERMISSIONS);
        } else {
            /** Permissions granted //
            Log.w("checkPermissions", "Permissions granted");
            locationPermissionsGranted = true;
            initializeListsInUtils();
            /** Initialize permission object in MapsApisUtils to make it available for MapViewfragment //
            MapsApisUtils.setPermissions(locationPermissionsGranted);
        }
    }
    */

    private void checkCurrentUserSelectionAndLaunchActivity() {
        // Get current user selected restaurant
        String selectionId = FirestoreUtils.getCurrentUser().getSelectionId();
        String selectionDate = FirestoreUtils.getCurrentUser().getSelectionDate();
        boolean isSelected = selectionId != null && currentDate.equals(selectionDate);
        if (isSelected) {
            // Get selected restaurant from restaurants collection in database
            RestaurantManager.getRestaurantData(selectionId)
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
            showSnackBar(getString(R.string.choice_error));
        }
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurant) {
        Intent intent = new Intent(MainActivity.this, DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void launchSettingActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logout(){
        userManager.signOut(this).addOnSuccessListener(aVoid -> closeActivity());
    }

    /*  // Replaced with access from FirestoreUtils using User model instead of FirebaseUser model

    // Update user information
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
    */

    // Update user information
    private void updateUIWithUserData(){
        // if(userManager.isCurrentUserLogged()){   // TODO : To be deleted
        if(FirestoreUtils.isCurrentUserLogged()){
            User user = FirestoreUtils.getCurrentUser();
            if(user.getUserUrlPicture() != null){
                setProfilePicture(user.getUserUrlPicture());
            }
            setTextUserData(user);
        }
    }

    // Update user picture
    private void setProfilePicture(String profilePictureUrl){
        userPicture.setImageTintList(null);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userPicture);
    }

    // Update user name and email
    private void setTextUserData(User user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getUserEmail()) ? getString(R.string.info_no_email_found) : user.getUserEmail();
        String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.info_no_username_found) : user.getUsername();
        //Update views with data
        userName.setText(username);
        userEmail.setText(email);
    }


    private void closeActivity() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }




























    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    // Declare main fragment
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    */


    /** To be commented if menu is handled in fragments */
    /*
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }
    */

    /** To be commented if menu is handled in fragments */
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                // toggleVisibility(autocompleteCardView);
                // if (autocompleteCardView.getVisibility() == View.VISIBLE) MapsApisUtils.configureAutocompleteSupportFragment(autocompleteFragment, this);
                toggleVisibility(searchView);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */


    /**
     * ---------------------
     * FRAGMENTS
     * ---------------------
     */

    /*
    private void configureAndShowMapViewFragment(){

        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        mMapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fm_lyt_map_view);

        // We only add MapViewFragment if found fm_lyt_map_view (in Tablet mode)
        if (mMapViewFragment == null && findViewById(R.id.fm_lyt_map_view) != null) {
            // Create new main fragment
            mMapViewFragment = new MapViewFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fm_lyt_map_view, mMapViewFragment)
                    .commit();
        }
    }

    private void configureAndShowListViewFragment(){

        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        mListViewFragment = (ListViewFragment) getSupportFragmentManager().findFragmentById(R.id.fm_lyt_list_view);

        // We only add ListViewFragment if found fm_lyt_list_view (in Tablet mode)
        if (mListViewFragment == null && findViewById(R.id.fm_lyt_list_view) != null) {
            // Create new main fragment
            mListViewFragment = new ListViewFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fm_lyt_list_view, mListViewFragment)
                    .commit();
        }
    }

    private void configureAndShowWorkmatesFragment(){

        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        mWorkmatesFragment = (WorkmatesFragment) getSupportFragmentManager().findFragmentById(R.id.fm_lyt_workmates);

        // We only add WorkmatesFragment if found fm_lyt_workmates (in Tablet mode)
        if (mWorkmatesFragment == null && findViewById(R.id.fm_lyt_workmates) != null) {
            // Create new main fragment
            mWorkmatesFragment = new WorkmatesFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fm_lyt_workmates, mWorkmatesFragment)
                    .commit();
        }
    }
    */






}