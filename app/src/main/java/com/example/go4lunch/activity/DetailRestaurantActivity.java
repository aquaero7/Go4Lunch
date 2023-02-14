package com.example.go4lunch.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.SelectedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.CalendarUtils;

import java.util.List;


public class DetailRestaurantActivity extends AppCompatActivity implements DetailRestaurantFragment.OnButtonClickedListener {

    // private Boolean isSelected; // TODO : Update status with information in database
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
    public void onButtonClicked(View view, FragmentDetailRestaurantBinding fragmentBinding, String rId, String rName, String rAddress, double rRating, List<Photo> rPhotos, boolean isSelected) {
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
                if (isSelected) {
                    addSelectionToDatabase(rId, rName, rAddress, rRating, rPhotos);
                    toastText = getString(R.string.fabChecked);
                } else {
                    removeSelectionFromDatabase(rId);
                    toastText = getString(R.string.fabUnchecked);
                }
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

    private void addSelectionToDatabase(String rId, String rName, String rAddress, double rRating, List<Photo> rPhotos) {
        // Add restaurant to selected restaurant collection in database
        SelectedRestaurantManager.getInstance().createSelectedRestaurant(rId, rName, rAddress, rRating, rPhotos);
        // Add selected restaurant ID to user document in database
        UserManager.getInstance().updateSelectionId(rId);
        UserManager.getInstance().updateSelectionDate(CalendarUtils.getCurrentDate());
    }

    private void removeSelectionFromDatabase(String rId) {
        /** DO NOT remove restaurant from selected restaurant collection in database
        because it can be also selected by others workmates !
        // SelectedRestaurantManager.getInstance().deleteSelectedRestaurant(rId); //
        */
        // Remove selected restaurant ID from user document in database
        UserManager.getInstance().updateSelectionId(null);
        UserManager.getInstance().updateSelectionDate(null);
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