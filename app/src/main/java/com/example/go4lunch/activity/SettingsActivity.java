package com.example.go4lunch.activity;

import android.os.Bundle;
import android.view.View;

import com.example.go4lunch.databinding.ActivitySettingsBinding;
import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.example.go4lunch.fragment.SettingsFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.utils.EventButtonClick;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding>
        implements SettingsFragment.OnButtonClickedListener {

    private String message;

    @Override
    ActivitySettingsBinding getViewBinding() {
        return ActivitySettingsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureAndShowSettingsFragment();
    }

    // --------------
    // CallBack
    // --------------
    @Override
    // Binding added as an argument to make it available here
    public void onButtonClicked(View view, FragmentSettingsBinding fragmentBinding,
                                String searchRadiusPrefs, String notificationsPrefs) {
        // Handle the button click event
        // String tag = String.valueOf(view.getTag());  // TODO : To be deleted
        // switch (tag) {   // TODO : To be deleted
        switch (EventButtonClick.from(view)) {
            // case "BTN_SAVE":    // TODO : To be deleted
            case BTN_SAVE:
                updateSearchRadiusPrefsInDatabase(searchRadiusPrefs);
                break;
            // case "SW_NOTIFICATION": // TODO : To be deleted
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
            settingsFragment = new SettingsFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayoutSettings, settingsFragment)
                    .commit();
        }
    }

    private void updateSearchRadiusPrefsInDatabase(String searchRadiusPrefs) {
        if (! Objects.equals(searchRadiusPrefs, "0") && ! searchRadiusPrefs.isEmpty()) {
            // Add search radius preference to user document in database
            UserManager.getInstance().updateSearchRadiusPrefs(searchRadiusPrefs);
            /** Update objects in Utils to make them available for fragments */
            MapsApisUtils.setSearchRadius(searchRadiusPrefs);
            FirestoreUtils.updateCurrentUser("RAD", searchRadiusPrefs);
            FirestoreUtils.updateWorkmatesList("RAD", searchRadiusPrefs);
            message = getString(R.string.search_radius_prefs_saved);
        } else {
            // Remove search radius preference from user document in database
            UserManager.getInstance().updateSearchRadiusPrefs(null);
            /** Update objects in Utils to make them available for fragments */
            MapsApisUtils.setSearchRadius(null);
            FirestoreUtils.updateCurrentUser("RAD", null);
            FirestoreUtils.updateWorkmatesList("RAD", null);
            message = getString(R.string.search_radius_prefs_deleted);
        }
        showSnackBar(message);
    }

    private void updateNotificationsPrefsInDatabase(String notificationsPrefs) {
        if (notificationsPrefs != null && ! notificationsPrefs.isEmpty()) {
            if (Boolean.parseBoolean(notificationsPrefs)) {
                // Add notifications preference to user document in database
                UserManager.getInstance().updateNotificationsPrefs(notificationsPrefs);
                /** Update object in FirestoreUtils to make it available for fragments */
                FirestoreUtils.updateCurrentUser("NOT", notificationsPrefs);
                FirestoreUtils.updateWorkmatesList("NOT", notificationsPrefs);
                message = getString(R.string.switch_checked);
            } else {
                // Remove notifications preference from user document in database
                UserManager.getInstance().updateNotificationsPrefs(null);
                /** Update object in FirestoreUtils to make it available for fragments */
                FirestoreUtils.updateCurrentUser("NOT", null);
                FirestoreUtils.updateWorkmatesList("NOT", null);
                message = getString(R.string.switch_unchecked);
            }
        showSnackBar(message);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}