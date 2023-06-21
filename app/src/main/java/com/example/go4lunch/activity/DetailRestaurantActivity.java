package com.example.go4lunch.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utilsforviews.EventButtonClick;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class DetailRestaurantActivity extends BaseActivity<ActivityDetailRestaurantBinding>
        implements DetailRestaurantFragment.OnButtonClickedListener {

    private String message;
    private LikedRestaurantManager likedRestaurantManager = LikedRestaurantManager.getInstance();
    private UserManager userManager = UserManager.getInstance();

    @Override
    ActivityDetailRestaurantBinding getViewBinding() {
        return ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowDetailRestaurantFragment();

    }

    // --------------
    // CallBack
    // --------------
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, FragmentDetailRestaurantBinding fragmentBinding,
                                String rId, String rName, String rAddress, String rPhoneNumber,
                                String rWebsite, double rRating, List<Photo> rPhotos,
                                boolean isSelected, boolean isLiked, String uId) {
        // Handle the button click event
        switch (EventButtonClick.from(view)) {
            case BTN_CALL:
                if (rPhoneNumber != null) callRestaurant(rPhoneNumber);
                break;
            case BTN_LIKE:
                updateLikeInDatabase(isLiked, rId, uId);
                break;
            case BTN_WEBSITE:
                if (rWebsite != null) displayRestaurantWebsite(rWebsite);
                break;
            case FAB_SELECT:
                updateSelectionInDatabase(isSelected, rId, rName, rAddress);
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

    private void updateSelectionInDatabase(boolean isSelected, String rId, String rName, String rAddress) {
        if (isSelected) {
            // Add selected restaurant ID to user document in database
            String currentDate = CalendarUtils.getCurrentDate();
            userManager.updateSelectionId(rId);
            userManager.updateSelectionDate(currentDate);
            userManager.updateSelectionName(rName);
            userManager.updateSelectionAddress(rAddress);
            message = getString(R.string.fab_checked);
        } else {
            // Remove selected restaurant ID from user document in database
            userManager.updateSelectionId(null);
            userManager.updateSelectionDate(null);
            userManager.updateSelectionName(null);
            userManager.updateSelectionAddress(null);
            message = getString(R.string.fab_unchecked);
        }
        showSnackBar(message);
    }

    private void callRestaurant(String rPhoneNumber){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rPhoneNumber)));
    }

    private void updateLikeInDatabase(boolean isLiked, String rId, String uId){
        if (isLiked) {
            likedRestaurantManager.createLikedRestaurant(rId+uId, rId, uId);
            message = getString(R.string.btn_like_checked);
        } else {
            likedRestaurantManager.deleteLikedRestaurant(rId+uId);
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