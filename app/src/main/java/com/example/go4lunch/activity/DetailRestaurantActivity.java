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

    LikedRestaurantManager likedRestaurantManager = LikedRestaurantManager.getInstance();

    UserViewModel userViewModel;


    @Override
    ActivityDetailRestaurantBinding getViewBinding() {
        return ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowDetailRestaurantFragment();
        initViewmodels();

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
        // String tag = String.valueOf(view.getTag());  // TODO : To be deleted
        // switch (tag) {   // TODO : To be deleted
        switch (EventButtonClick.from(view)) {
            // case "BTN_CALL": // TODO : To be deleted
            case BTN_CALL:
                if (rPhoneNumber != null) callRestaurant(rPhoneNumber);
                break;
            // case "BTN_LIKE": // TODO : To be deleted
            case BTN_LIKE:
                updateLikeInDatabase(isLiked, rId, uId);
                break;
            // case "BTN_WEBSITE":  // TODO : To be deleted
            case BTN_WEBSITE:
                if (rWebsite != null) displayRestaurantWebsite(rWebsite);
                break;
            // case "FAB_SELECT":  // TODO : To be deleted
            case FAB_SELECT:
                updateSelectionInDatabase(isSelected, rId);
                break;
        }
    }

    private void configureAndShowDetailRestaurantFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        DetailRestaurantFragment detailRestaurantFragment =
                (DetailRestaurantFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutDetailRestaurant);
        if (detailRestaurantFragment == null) {
            // Create new detail restaurant fragment
            detailRestaurantFragment = new DetailRestaurantFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutDetailRestaurant, detailRestaurantFragment)
                    .commit();
        }
    }

    private void initViewmodels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void updateSelectionInDatabase(boolean isSelected, String rId) {
        if (isSelected) {
            // Add selected restaurant ID to user document in database
            String currentDate = CalendarUtils.getCurrentDate();
            UserManager.getInstance().updateSelectionId(rId);
            UserManager.getInstance().updateSelectionDate(currentDate);
            /** Update objects in FirestoreUtils to make them available for notifications */    // TODO : To be deleted cause done in DetailRestaurantFragment
            // FirestoreUtils.updateCurrentUser(rId, currentDate);  // TODO : To be deleted cause done in DetailRestaurantFragment
            // FirestoreUtils.updateWorkmatesList(rId, currentDate);    // TODO : To be deleted cause done in DetailRestaurantFragment
            message = getString(R.string.fab_checked);
        } else {
            // Remove selected restaurant ID from user document in database
            UserManager.getInstance().updateSelectionId(null);
            UserManager.getInstance().updateSelectionDate(null);
            /** Update objects in FirestoreUtils to make them available for notifications */    // TODO : To be deleted cause done in DetailRestaurantFragment
            // FirestoreUtils.updateCurrentUser(null, null);    // TODO : To be deleted cause done in DetailRestaurantFragment
            // FirestoreUtils.updateWorkmatesList(null, null);  // TODO : To be deleted cause done in DetailRestaurantFragment
            message = getString(R.string.fab_unchecked);
        }
        showSnackBar(message);

        /*  // TODO : To be deleted
        /** Update object workmatesList in FirestoreUtils
         only to make selection changes available for fragments //
        List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
        */
    }

    private void callRestaurant(String rPhoneNumber){
        /*
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + rPhoneNumber));
        startActivity(dialIntent);
        */
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

        /*  // TODO : To be deleted
        /** Update objects likedRestaurantsList and workmatesLists in FirestoreUtils
         only to make likes changes available for fragments //
        List<LikedRestaurant> likedRestaurantsList = FirestoreUtils.getLikedRestaurantsListFromDatabaseDocument();
        // List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
        */
        /** Update object likedRestaurantsList in FirestoreUtils to make it available for fragments */
        FirestoreUtils.updateLikedRestaurantsList(isLiked,rId, uId);
    }

    private void displayRestaurantWebsite(String rWebsite){
        /*
        Intent webViewIntent = new Intent(Intent.ACTION_VIEW);
        webViewIntent.setData(Uri.parse(rWebsite));
        startActivity(webViewIntent);
        */
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rWebsite)));
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}