package com.example.go4lunch.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.go4lunch.Fragments.MapViewFragment;
import com.example.go4lunch.Fragments.PagerAdapter;
import com.example.go4lunch.Fragments.RestaurantFragment;
import com.example.go4lunch.Fragments.SettingFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener {

    // For Navigation Drawer design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // For fragments
    // 1 - Declare fragment handled by Navigation Drawer
    private Fragment fragmentRestaurant;
    private Fragment fragmentSetting;

    // For datas
    // 2 - Identify each fragment with a number
    private static final int FRAGMENT_RESTAURANT = 0;
    private static final int FRAGMENT_SETTING = 1;

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
        //setContentView(R.layout.activity_main);  // Useless with initBinding in BaseActivity ?

        // Get the toolbar view
        this.configureToolbar();

        /*
        this.configureAndShowMapViewFragment();
        this.configureAndShowListViewFragment();
        this.configureAndShowWorkmatesFragment();
        */

        // Configure ViewPager and tabs
        this.configureViewPagerAndTabs();

        // Configure Navigation Drawer views
        this.configureDrawerLayout();
        this.configureNavigationView();
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
                this.showFragment(FRAGMENT_RESTAURANT);
                break;
            case R.id.activity_main_drawer_settings:
                this.showFragment(FRAGMENT_SETTING);
                break;
            case R.id.activity_main_drawer_logout:
                // TODO
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
            Toolbar toolbar = findViewById(R.id.toolbar);
            */

            // // Case 2 : With Navigation Drawer
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        ViewPager2 pager = (ViewPager2)findViewById(R.id.activity_main_viewpager);

        // Set Adapter PagerAdapter and glue it together
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle()));

        // Get TabLayout from layout
        TabLayout tabs= (TabLayout)findViewById(R.id.activity_main_tabs);

        // Glue TabLayout and ViewPager together
        new TabLayoutMediator(tabs, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // Setup tab title
                tab.setText(tabTitles[position]);
                // Setup tab icon
                tab.setIcon(tabIcons[position]);
            }
        }).attach();

        // Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);

    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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


    // 5 - Show fragment according an Identifier
    private void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
            case FRAGMENT_RESTAURANT:
                this.showRestaurantFragment();
                break;
            case FRAGMENT_SETTING:
                this.showSettingFragment();
                break;
            default:
                break;
        }
    }


    // 4 - Create each fragment page and show it

    private void showRestaurantFragment(){
        if (this.fragmentRestaurant == null) this.fragmentRestaurant = RestaurantFragment.newInstance();
        this.startTransactionFragment(this.fragmentRestaurant);
    }

    private void showSettingFragment(){
        if (this.fragmentSetting == null) this.fragmentSetting = SettingFragment.newInstance();
        this.startTransactionFragment(this.fragmentSetting);
    }


    // 3 - Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment).commit();
        }
    }


}