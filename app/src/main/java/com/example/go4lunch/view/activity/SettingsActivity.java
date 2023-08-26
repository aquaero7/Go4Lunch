package com.example.go4lunch.view.activity;

import android.os.Bundle;
import android.view.View;

import com.example.go4lunch.databinding.ActivitySettingsBinding;
import com.example.go4lunch.utils.EventObjectClick;
import com.example.go4lunch.view.fragment.SettingsFragment;
import com.example.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding>
        implements SettingsFragment.OnButtonClickedListener {

    @Override
    ActivitySettingsBinding getViewBinding() {
        return ActivitySettingsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowSettingsFragment();
    }

    /** CallBack */
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, String message) {
        // Handle the button click event
        switch (EventObjectClick.fromView(view)) {
            case BTN_SAVE:
            case SW_NOTIFICATION:
                if (message != null) Snackbar
                        .make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
    }

    private void configureAndShowSettingsFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutSettings);
        if (settingsFragment == null) {
            // Create instance of settings fragment
            settingsFragment = SettingsFragment.newInstance();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutSettings, settingsFragment)
                    .commit();
        }
    }

}