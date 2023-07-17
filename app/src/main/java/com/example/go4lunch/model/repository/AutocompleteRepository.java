package com.example.go4lunch.model.repository;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

public class AutocompleteRepository {

    public static volatile AutocompleteRepository instance;
    private ActivityResultLauncher<Intent> startAutocomplete;

    public AutocompleteRepository() {
    }

    public static AutocompleteRepository getInstance() {
        AutocompleteRepository result = instance;
        if (result != null) {
            return result;
        } else {
            instance = new AutocompleteRepository();
            return  instance;
        }
    }

    public ActivityResultLauncher<Intent> getStartAutocomplete() {
        return startAutocomplete;
    }

    public void setStartAutocomplete(ActivityResultLauncher<Intent> startAutocomplete) {
        this.startAutocomplete = startAutocomplete;
    }

}
