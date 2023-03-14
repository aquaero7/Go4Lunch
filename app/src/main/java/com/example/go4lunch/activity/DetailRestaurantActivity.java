package com.example.go4lunch.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.LikedRestaurantManager;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class DetailRestaurantActivity extends BaseActivity<ActivityDetailRestaurantBinding>
        implements DetailRestaurantFragment.OnButtonClickedListener {

    private String message;

    LikedRestaurantManager likedRestaurantManager = LikedRestaurantManager.getInstance();


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
        String tag = String.valueOf(view.getTag());
        switch (tag) {
            case "BTN_CALL":
                if (rPhoneNumber!= null) callRestaurant(rPhoneNumber);
                break;
            case "BTN_LIKE":
                updateLike(isLiked, rId, uId);
                break;
            case "BTN_WEBSITE":
                if (rWebsite != null) displayRestaurantWebsite(rWebsite);
                break;
            case "FAB":
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

    private void updateSelectionInDatabase(boolean isSelected, String rId) {
        if (isSelected) {
            // Add selected restaurant ID to user document in database
            UserManager.getInstance().updateSelectionId(rId);
            UserManager.getInstance().updateSelectionDate(CalendarUtils.getCurrentDate());
            message = getString(R.string.fabChecked);
        } else {
            // Remove selected restaurant ID from user document in database
            UserManager.getInstance().updateSelectionId(null);
            UserManager.getInstance().updateSelectionDate(null);
            message = getString(R.string.fabUnchecked);
        }
        showSnackBar(message);

        /** Update objects workmatesList in FirestoreUtils
         only to make selection changes available for Workmates fragment */
        List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
    }

    private void callRestaurant(String rPhoneNumber){
        /*
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + rPhoneNumber));
        startActivity(dialIntent);
        */
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rPhoneNumber)));
    }

    private void updateLike(boolean isLiked, String rId, String uId){
        if (isLiked) {
            likedRestaurantManager.createLikedRestaurant(rId+uId, rId, uId);
            message = getString(R.string.btnLikeChecked);
        } else {
            likedRestaurantManager.deleteLikedRestaurant(rId+uId);
            message = getString(R.string.btnLikeUnchecked);
        }
        showSnackBar(message);

        /** Update objects likedRestaurantsList in FirestoreUtils
         only to make likes changes available for DetailRestaurant fragment */
        List<LikedRestaurant> likedRestaurantsList = FirestoreUtils.getLikedRestaurantsListFromDatabaseDocument();
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