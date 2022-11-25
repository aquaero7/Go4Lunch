package com.example.go4lunch.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewbinding.ViewBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.go4lunch.Fragments.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DetailRestaurantActivity extends AppCompatActivity implements DetailRestaurantFragment.OnButtonClickedListener {

    private Boolean fabChecked = true; // TODO : Update status with information in database
    private String toastText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        configureAndShowDetailRestaurantFragment();
    }

    // --------------
    // CallBack
    // --------------
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, FragmentDetailRestaurantBinding fragmentBinding) {
        // Handle the button click event
        String tag = String.valueOf(view.getTag());
        switch (tag) {
            case "BTN_CALL":
                callRestaurant();
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "BTN_LIKE":
                likeRestaurant();
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "BTN_WEBSITE":
                displayRestaurantWebsite();
                toastText = tag;    // TODO : Delete after action completion
                break;
            case "FAB":
                // FloatingActionButton selectionFab = findViewById(R.id.selection_fab);    // TODO : To be deleted cause replaced with ViewBinding
                FloatingActionButton selectionFab = fragmentBinding.selectionFab;   // TODO : to be implemented IF WORKING in place of findViewById
                // Toggle FAB status
                fabChecked = !fabChecked;
                // Update toast text
                toastText = (fabChecked) ? getString(R.string.fabChecked) : getString(R.string.fabUnchecked);
                // Launch actions
                toggleSelectionFabDisplay(selectionFab);
                updateDatabase();
                break;
        }
        displayToast();
    }

    private void configureAndShowDetailRestaurantFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        DetailRestaurantFragment detailRestaurantFragment = (DetailRestaurantFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutDetailRestaurant);
        if (detailRestaurantFragment == null) {
            // Create new detail restaurant fragment
            detailRestaurantFragment = new DetailRestaurantFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutDetailRestaurant, detailRestaurantFragment)
                    .commit();
        }
    }

    private void toggleSelectionFabDisplay(FloatingActionButton fab) {
        // Define the foreground of the mipmap, according to new FAB status
        int resId = (fabChecked) ? R.mipmap.im_check_green_white : R.mipmap.im_check_grey_white;
        // Set up the new foreground
        fab.setForeground(AppCompatResources.getDrawable(this,resId));
    }

    private void updateDatabase() {
        // TODO : Update status in database
    }

    private void displayToast() {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    private void callRestaurant(){
        // TODO
    }

    private void likeRestaurant(){
        // TODO
    }

    private void displayRestaurantWebsite(){
        // TODO
    }


}