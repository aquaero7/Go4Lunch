package com.example.go4lunch.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.go4lunch.Fragments.ListViewFragment;
import com.example.go4lunch.Fragments.MapViewFragment;
import com.example.go4lunch.Fragments.PagerAdapter;
import com.example.go4lunch.Fragments.WorkmatesFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    // 1 - Declare main fragment
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;

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
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    private void configureToolbar(){
        // Get the toolbar view inside the activity layout

            //  // Case 1 : Using findViewById()
            // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            Toolbar toolbar = findViewById(R.id.toolbar);
            //

            /*  // Cas 2 : Using View Binding
            ActivityMainBinding binding = getViewBinding();
            setContentView(binding.getRoot());
            Toolbar toolbar = binding.toolbar;
            */

        // Sets the Toolbar
        setSupportActionBar(toolbar);

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

    private void configureViewPagerAndTabs(){

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
                String [] tabTiles={"Map View","List View","Workmates"};
                tab.setText(tabTiles[position]);
            }
        }).attach();

        // Design purpose. Tabs have the same width
        //tabs.setTabMode(TabLayout.MODE_FIXED);


    }

}