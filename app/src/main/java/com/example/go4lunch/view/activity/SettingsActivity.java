package com.example.go4lunch.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.databinding.ActivitySettingsBinding;
import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.example.go4lunch.view.fragment.SettingsFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.EventButtonClick;
import com.example.go4lunch.viewmodel.SettingsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding>
        implements SettingsFragment.OnButtonClickedListener {

    private SettingsViewModel settingsViewModel;
    private String message;

    @Override
    ActivitySettingsBinding getViewBinding() {
        return ActivitySettingsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowSettingsFragment();
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    // --------------
    // CallBack
    // --------------
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, FragmentSettingsBinding fragmentBinding,
                                String searchRadiusPrefs, String notificationsPrefs) {
        // Handle the button click event
        switch (EventButtonClick.from(view)) {
            case BTN_SAVE:
                updateSearchRadiusPrefs(searchRadiusPrefs);
                break;
            case SW_NOTIFICATION:
                updateNotificationsPrefsInDatabase(notificationsPrefs);
                break;
        }
    }

    private void configureAndShowSettingsFragment() {
        // Get FragmentManager (Support) and try to find existing instance of fragment in FrameLayout container
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutSettings);
        if (settingsFragment == null) {
            // Create new settings fragment
            settingsFragment = SettingsFragment.newInstance(); // instead of : new SettingsFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutSettings, settingsFragment)
                    .commit();
        }
    }

    private void updateSearchRadiusPrefs(String searchRadiusPrefs) {
        if (! Objects.equals(searchRadiusPrefs, "0") && ! searchRadiusPrefs.isEmpty()) {
            // Add search radius preference to current user
            settingsViewModel.updateSearchRadiusPrefs(searchRadiusPrefs);
            message = getString(R.string.search_radius_prefs_saved);
        } else {
            // Remove search radius preference from current user
            settingsViewModel.updateSearchRadiusPrefs(null);
            message = getString(R.string.search_radius_prefs_deleted);
        }
        showSnackBar(message);
    }

    private void updateNotificationsPrefsInDatabase(String notificationsPrefs) {
        if (notificationsPrefs != null && ! notificationsPrefs.isEmpty()) {
            if (Boolean.parseBoolean(notificationsPrefs)) {
                // Add notifications preference to current user
                settingsViewModel.updateNotificationsPrefs(notificationsPrefs);
                message = getString(R.string.switch_checked);
            } else {
                // Remove notifications preference from current user
                settingsViewModel.updateNotificationsPrefs(null);
                message = getString(R.string.switch_unchecked);
            }
        showSnackBar(message);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}