package com.example.go4lunch.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.MapsApisUtils;
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

        // Initialize preferences
        mRadiusEditText.setText(MapsApisUtils.getSearchRadius());
        mNotificationSwitch.setChecked(Boolean.parseBoolean(FirestoreUtils.getCurrentUser().getNotificationsPrefs()));

        //Set listeners on buttons and switch
        mSaveButton.setOnClickListener(this);
        mNotificationSwitch.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity */
    public void onClick(View v) {
        String tag = String.valueOf(v.getTag());
        switch (tag) {
            case "BTN_SAVE":
                hideVirtualKeyboard(getContext(), v);
                searchRadiusPrefs = mRadiusEditText.getText().toString();
            case "SW_NOTIFICATION":
                notificationsPrefs = String.valueOf(mNotificationSwitch.isChecked());
                // updatePreferencesInFirestoreUtils(tag);  // TODO: To be deleted cause done in SettingsActivity
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

    public void hideVirtualKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}