package com.example.go4lunch.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.Fragments.PagerAdapter;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.manager.UserManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener {

    // For Navigation Drawer design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView userPicture;
    private TextView userName;
    private TextView userEmail;


    private final UserManager userManager = UserManager.getInstance();


    /*
    // Declare main fragment
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    */

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

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
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
                launchDetailRestaurantActivity();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                Toast.makeText(this, "Recherche indisponible, demandez plutôt l'avis de Google, c'est mieux et plus rapide.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * ---------------------
    * CONFIGURATIONS
    * ---------------------
    */

    private void configureToolbar(){
        // Get the toolbar view inside the activity layout

        /*  // Case 1 : Without Navigation Drawer
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Toolbar toolbar = findViewById(R.id.toolbar);   // TODO : To be deleted cause replaced with ViewBinding
        Toolbar toolbar = binding.includedToolbar.toolbar;
        */

        // // Case 2 : With Navigation Drawer
        // this.toolbar = (Toolbar) findViewById(R.id.toolbar); // TODO : To be deleted cause replaced with ViewBinding
        this.toolbar = binding.includedToolbar.toolbar;
        //

        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    private void configureViewPagerAndTabs(){

        // Initialize title list
        String [] tabTitles={"Map View","List View","Workmates"};

        // Initialize icon list
        int[] tabIcons = {
                R.drawable.ic_baseline_map_black_24,
                R.drawable.ic_baseline_view_list_black_24,
                R.drawable.ic_baseline_group_black_24
        };

        // Get ViewPager from layout
        // ViewPager2 pager = (ViewPager2)findViewById(R.id.activity_main_viewpager);   // TODO : To be deleted cause replaced with ViewBinding
        ViewPager2 pager = binding.activityMainViewpager;

        // Set Adapter PagerAdapter and glue it together
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle()));

        // Get TabLayout from layout
        // TabLayout tabs = (TabLayout)findViewById(R.id.activity_main_tabs);   // TODO : To be deleted cause replaced with ViewBinding
        TabLayout tabs = binding.activityMainTabs;

        // Glue TabLayout and ViewPager together
        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            // Setup tab title
            tab.setText(tabTitles[position]);
            // Setup tab icon
            tab.setIcon(tabIcons[position]);
        }).attach();

        // Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        // this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);   // TODO : To be deleted cause replaced with ViewBinding
        this.drawerLayout = binding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    // Configure NavigationView
    private void configureNavigationView(){
        // this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);   // TODO : To be deleted cause replaced with ViewBinding
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


    /*
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

    private void launchDetailRestaurantActivity() {
        Intent intent = new Intent(MainActivity.this, DetailRestaurantActivity.class);
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

}