package com.example.go4lunch.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.example.go4lunch.utils.EventButtonClick;
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.viewmodel.SettingsViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private FragmentSettingsBinding binding;
    private OnButtonClickedListener mCallback;
    private SettingsViewModel settingsViewModel;

    // Constructor
    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return (new SettingsFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        // Initialize ViewModel
        settingsViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(SettingsViewModel.class);
        // Initialize preferences
        initPrefs();
        //Set listeners on buttons and switch
        setListeners();

        return binding.getRoot();
    }

    @Override
    // Spread the click to the parent activity
    public void onClick(View v) {
        String message = null;
        switch (EventButtonClick.from(v)) {
            case BTN_SAVE:
                Utils.getInstance().hideVirtualKeyboard(requireContext(), v);
                message = settingsViewModel.updateSearchRadiusPrefs(binding.etRadius.getText().toString());
                // Display default radius if '0' or 'empty' saved as prefs
                if (message == getString(R.string.search_radius_prefs_deleted))
                    binding.etRadius.setText(settingsViewModel.getSearchRadius(settingsViewModel.getCurrentUser()));
                break;
            case SW_NOTIFICATION:
                message = settingsViewModel.updateNotificationsPrefs(String.valueOf(binding.switchNotification.isChecked()));
                break;
        }
        mCallback.onButtonClicked(v, message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    // Declare an interface that will be implemented by any container activity for callback
    public interface OnButtonClickedListener {
        void onButtonClicked(View view, String message);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (SettingsFragment.OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e + " must implement OnButtonClickedListener");
        }
    }

    private void initPrefs() {
        // Search Radius prefs
        binding.etRadius.setText(settingsViewModel.getSearchRadius(settingsViewModel.getCurrentUser()));
        // Notifications prefs
        binding.switchNotification.setChecked(Boolean.parseBoolean(settingsViewModel.getNotificationsPrefs(settingsViewModel.getCurrentUser())));
    }

    private void setListeners() {
        binding.buttonSave.setOnClickListener(this);
        binding.switchNotification.setOnClickListener(this);
    }

}