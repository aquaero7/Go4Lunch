package com.example.go4lunch.view.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.go4lunch.databinding.ActivityDetailRestaurantBinding;
import com.example.go4lunch.view.fragment.DetailRestaurantFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.EventButtonClick;
import com.google.android.material.snackbar.Snackbar;

public class DetailRestaurantActivity extends BaseActivity<ActivityDetailRestaurantBinding>
        implements DetailRestaurantFragment.OnButtonClickedListener {

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
    public void onButtonClicked(View view, String message, String rPhoneNumber, String rWebsite) {
        // Handle the button click event
        switch (EventButtonClick.from(view)) {
            case BTN_CALL:
                if (rPhoneNumber != null)
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rPhoneNumber)));
                break;
            case BTN_WEBSITE:
                if (rWebsite != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rWebsite)));
                break;
            case BTN_LIKE:
            case FAB_SELECT:
                if (message != null) Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
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

}