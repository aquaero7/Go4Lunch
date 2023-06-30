package com.example.go4lunch.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.EventButtonClick;
import com.example.go4lunch.viewmodel.SettingsViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    // Declare callback
    private OnButtonClickedListener mCallback;
    // Declare ViewBinding
    private FragmentSettingsBinding binding;
    // Declare View items
    private EditText mRadiusEditText;
    private Button mSaveButton;
    private SwitchMaterial mNotificationSwitch;
    private String searchRadiusPrefs;
    private String notificationsPrefs;
    private SettingsViewModel settingsViewModel;
    private User currentUser;


    // Constructor
    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return (new SettingsFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        // Initialize View items
        mRadiusEditText = binding.etRadius;
        mSaveButton = binding.buttonSave;
        mNotificationSwitch = binding.switchNotification;

        // Initialize ViewModel
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        // Initialize data
        initData();

        // Initialize preferences

        // Search Radius prefs
        mRadiusEditText.setText(settingsViewModel.getSearchRadius(currentUser));
        // Notifications prefs
        mNotificationSwitch.setChecked(Boolean.parseBoolean(settingsViewModel.getNotificationsPrefs(currentUser)));

        //Set listeners on buttons and switch
        mSaveButton.setOnClickListener(this);
        mNotificationSwitch.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity */
    public void onClick(View v) {
        switch (EventButtonClick.from(v)) {
            case BTN_SAVE:
                hideVirtualKeyboard(requireContext(), v);
                searchRadiusPrefs = mRadiusEditText.getText().toString();
                break;
            case SW_NOTIFICATION:
                notificationsPrefs = String.valueOf(mNotificationSwitch.isChecked());
                break;
        }
        mCallback.onButtonClicked(v, binding, searchRadiusPrefs, notificationsPrefs);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    /* Declare an interface that will be implemented by any container activity for callback
        Binding added as an argument to make it available in the activity */
    public interface OnButtonClickedListener {
        void onButtonClicked(View view, FragmentSettingsBinding binding, String searchRadiusPrefs, String notificationsPrefs);
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

    private void initData() {
        currentUser = settingsViewModel.getCurrentUser();
    }

    public void hideVirtualKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}