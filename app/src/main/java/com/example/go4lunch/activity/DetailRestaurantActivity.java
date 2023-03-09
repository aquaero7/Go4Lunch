package com.example.go4lunch.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class DetailRestaurantActivity extends BaseActivity<ActivityDetailRestaurantBinding> implements DetailRestaurantFragment.OnButtonClickedListener {

    private String message;
    private String toastText;   // TODO : Delete after action completion


    @Override
    ActivityDetailRestaurantBinding getViewBinding() {
        return ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_detail_restaurant); // TODO : To be deleted cause this activity extends BaseActivity and overrides getViewBinding

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
                    addSelectionToDatabase(rId);
                    message = getString(R.string.fabChecked);
                } else {
                    removeSelectionFromDatabase(rId);
                    message = getString(R.string.fabUnchecked);
                }
                toastText = tag;    // TODO : Delete after action completion
                showSnackBar(message);

                /** Update object workmatesList in FirestoreUtils
                 in order to only make selection changes available for workmates fragment */
                List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
                break;
        }
        displayToast(); // TODO : Delete after action completion
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

    private void addSelectionToDatabase(String rId) {
        // Add selected restaurant ID to user document in database
        UserManager.getInstance().updateSelectionId(rId);
        UserManager.getInstance().updateSelectionDate(CalendarUtils.getCurrentDate());
    }

    private void removeSelectionFromDatabase(String rId) {
        // Remove selected restaurant ID from user document in database
        UserManager.getInstance().updateSelectionId(null);
        UserManager.getInstance().updateSelectionDate(null);
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

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void displayToast() {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }


}