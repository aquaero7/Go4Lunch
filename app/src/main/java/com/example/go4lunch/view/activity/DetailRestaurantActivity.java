package com.example.go4lunch.view.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.view.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.utils.EventButtonClick;
import com.example.go4lunch.viewmodel.DetailRestaurantViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class DetailRestaurantActivity extends BaseActivity<ActivityDetailRestaurantBinding>
        implements DetailRestaurantFragment.OnButtonClickedListener {

    private DetailRestaurantViewModel detailRestaurantViewModel;

    private User currentUser;
    private String message;

    @Override
    ActivityDetailRestaurantBinding getViewBinding() {
        return ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowDetailRestaurantFragment();
        // Initialize ViewModel
        detailRestaurantViewModel = new ViewModelProvider(this).get(DetailRestaurantViewModel.class);
        // Get data from calling activity
        getIntentData();
    }

    // --------------
    // CallBack
    // --------------
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, FragmentDetailRestaurantBinding fragmentBinding,
                                String rId, String rName, String rAddress, String rPhoneNumber,
                                String rWebsite, double rRating, List<Photo> rPhotos,
                                boolean isSelected, boolean isLiked) {
        // Handle the button click event
        switch (EventButtonClick.from(view)) {
            case BTN_CALL:
                if (rPhoneNumber != null) callRestaurant(rPhoneNumber);
                break;
            case BTN_LIKE:
                updateLike(isLiked, rId);
                break;
            case BTN_WEBSITE:
                if (rWebsite != null) displayRestaurantWebsite(rWebsite);
                break;
            case FAB_SELECT:
                updateSelection(isSelected, rId, rName, rAddress);
                break;
        }
    }

    private void configureAndShowDetailRestaurantFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        DetailRestaurantFragment detailRestaurantFragment =
                (DetailRestaurantFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutDetailRestaurant);
        if (detailRestaurantFragment == null) {
            // Create instance of detail restaurant fragment (instead of new detail restaurant fragment) TODO : To be confirmed
            detailRestaurantFragment = DetailRestaurantFragment.newInstance();  // instead of : new DetailRestaurantFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutDetailRestaurant, detailRestaurantFragment)
                    .commit();
        }
    }

    // Get restaurant from calling activity
    private void getIntentData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                currentUser = (User) bundle.getSerializable("CURRENT_USER");
                Log.w("DetailRestaurantActivity", "Name of current user : " + currentUser.getUsername());
            }
        }
    }

    private void updateSelection(boolean isSelected, String rId, String rName, String rAddress) {
        if (isSelected) {
            // Add selected restaurant ID to current user
            detailRestaurantViewModel.createSelection(rId, rName, rAddress);
            message = getString(R.string.fab_checked);
        } else {
            // Remove selected restaurant ID from current user
            detailRestaurantViewModel.deleteSelection();
            message = getString(R.string.fab_unchecked);
        }
        showSnackBar(message);
    }

    private void callRestaurant(String rPhoneNumber){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rPhoneNumber)));
    }

    private void updateLike(boolean isLiked, String rId){
        if (isLiked) {
            detailRestaurantViewModel.createLikedRestaurant(currentUser, rId);
            message = getString(R.string.btn_like_checked);
        } else {
            detailRestaurantViewModel.deleteLikedRestaurant(currentUser, rId);
            message = getString(R.string.btn_like_unchecked);
        }
        showSnackBar(message);
    }

    private void displayRestaurantWebsite(String rWebsite){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rWebsite)));
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}