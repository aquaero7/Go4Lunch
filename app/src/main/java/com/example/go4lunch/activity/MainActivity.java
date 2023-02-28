package com.example.go4lunch.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.FragmentAutocompleteBinding;
import com.example.go4lunch.fragment.MapViewFragment;
import com.example.go4lunch.fragment.PagerAdapter;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.SelectedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.MapsApisUtils;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, EventListener {

    // For Navigation Drawer design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // For ViewPager and Tabs
    private final String MAP_VIEW_TAB_TITLE = "Map View";
    private final String LIST_VIEW_TAB_TITLE = "List View";
    private final String WORKMATES_TAB_TITLE = "Workmates";
    private ViewPager2 pager;
    private TabLayout tabs;

    private ImageView userPicture;
    private TextView userName;
    private TextView userEmail;

    private Fragment fmt;
    private SearchView searchView;

    private final UserManager userManager = UserManager.getInstance();


    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.activity_main);  // Useless with initBinding in BaseActivity ?
        /*
        this.configureAndShowMapViewFragment();
        this.configureAndShowListViewFragment();
        this.configureAndShowWorkmatesFragment();
        */

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


    }

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
                checkCurrentUserSelection();
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
    * CONFIGURATIONS
    * ---------------------
    */

    private void configureToolbar(){
        // Get the toolbar view inside the activity layout

        /*  // Case 1 : Without Navigation Drawer
        Toolbar toolbar = binding.includedToolbar.toolbar;
        */

        // // Case 2 : With Navigation Drawer
        this.toolbar = binding.includedToolbar.toolbar;
        //

        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    private void configureViewPagerAndTabs(){

        // Initialize title list
        String [] tabTitles={MAP_VIEW_TAB_TITLE,LIST_VIEW_TAB_TITLE,WORKMATES_TAB_TITLE};

        // Initialize icon list
        int[] tabIcons = {
                R.drawable.ic_baseline_map_black_24,
                R.drawable.ic_baseline_view_list_black_24,
                R.drawable.ic_baseline_group_black_24
        };

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
                // TODO : Autocomplete transferred to MapViewFragment
                // autocompleteCardView.setVisibility(View.GONE);
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
                        Toast.makeText(MainActivity.this, "Query is : " + query, Toast.LENGTH_SHORT).show();
                        ((MapViewFragment)fmt).launchAutocomplete(query);
                        break;
                    case "f1":
                        Toast.makeText(MainActivity.this, "Query is : " + query, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Search not yet implemented on ListView\nQuery is : " + query, Toast.LENGTH_SHORT).show();    // TODO : To be replaced by action
                        // TODO : Do action
                        break;
                    case "f2":
                        Toast.makeText(MainActivity.this, "Query is : " + query, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Search not implemented on Workmates\nQuery is : " + query, Toast.LENGTH_SHORT).show();   // TODO : To be replaced by action
                        // TODO : Do action
                        break;
                }
                searchView.setQuery("", false);
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
                            Toast.makeText(MainActivity.this, "Query is : " + newText, Toast.LENGTH_SHORT).show();
                            searchView.setQuery("", false);
                            searchView.setVisibility(View.GONE);
                            ((MapViewFragment)fmt).launchAutocomplete(newText);
                        }
                        break;
                    case "f1":
                        //Toast.makeText(MainActivity.this, "Query is : " + newText, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Search not yet implemented on ListView\nQuery is : " + newText, Toast.LENGTH_SHORT).show();
                        break;
                    case "f2":
                        Toast.makeText(MainActivity.this, "Query is : " + newText, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Search not implemented on Workmates\nQuery is : " + newText, Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });
    }

    private void checkCurrentUserSelection() {
        // Get current user selected restaurant id from database
        UserManager.getInstance().getCurrentUserData().addOnSuccessListener(user -> {
            // Get current user selected restaurant id from database
            String selectionId = user.getSelectionId();
            if (selectionId != null) {
                // Get selected restaurant from restaurants collection in database
                // RestaurantManager.getRestaurantData(selectionId)
                SelectedRestaurantManager.getSelectedRestaurantData(selectionId)
                        .addOnSuccessListener(restaurant -> {
                            Log.w("MainActivity", "success task getRestaurantData");
                            launchDetailRestaurantActivity(restaurant);
                        })
                        .addOnFailureListener(e -> Log.w("MainActivity", e.getMessage()));
            } else {
                Toast.makeText(this, getString(R.string.choice_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void launchDetailRestaurantActivity(Restaurant restaurant) {
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

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }




















    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    // Declare main fragment
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    */

    /*  // TODO : To be deleted cause transferred to MapViewFragment
    // Declare the AutocompleteSupportFragment.
    private FragmentAutocompleteBinding fragmentAutocompleteBinding;
    private CardView autocompleteCardView;
    private AutocompleteSupportFragment autocompleteFragment;
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
                Toast.makeText(this, "Click on search button in MainActivity", Toast.LENGTH_LONG).show();   // TODO : To be deleted
                // configureAutocompleteSupportFragment();  // TODO : To be deleted
                // toggleVisibility(autocompleteCardView);
                // if (autocompleteCardView.getVisibility() == View.VISIBLE) MapsApisUtils.configureAutocompleteSupportFragment(autocompleteFragment, this);
                toggleVisibility(searchView);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */


    /*  // TODO : To be deleted cause transferred to MapViewFragment
    // Configure AutocompleteSupportFragment
    private void configureAutocompleteSupportFragment() {

        // Specify the limitation to only show results within the defined region
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LatLng home = MapsApisUtils.getDeviceLocation(true, fusedLocationProviderClient, this);
        int radius = MapsApisUtils.getDefaultRadius();
        LatLngBounds latLngBounds = DataProcessingUtils.calculateBounds(home, radius);
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast));


        // Get current tab
        String currentTabName = tabs.getTabAt(tabs.getSelectedTabPosition()).getText().toString();
        // Display autocomplete search menu only for Map and List Views
        if (currentTabName.equals(MAP_VIEW_TAB_TITLE) || currentTabName.equals(LIST_VIEW_TAB_TITLE)) {
            toggleVisibility(autocompleteCardView);
            if (autocompleteCardView.getVisibility() == View.VISIBLE) {

                // Set up a PlaceSelectionListener to handle the response.
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        // TODO: Get info about the selected place.
                        LatLng latLng = place.getLatLng();
                        double latitude = latLng.latitude;
                        double longitude = latLng.longitude;
                        Log.i("MainActivity", "Place: " + place.getName() + ", " + place.getId() + ", " + latitude + ", " + longitude);
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                        // TODO: Handle the error.
                        Log.i("MainActivity", "An error occurred: " + status);
                    }

                });

            }

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