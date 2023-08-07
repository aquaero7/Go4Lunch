package com.example.go4lunch.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.utils.Utils;

public class ViewModelFactory implements ViewModelProvider.Factory {

    Application application; // For AuthViewModel and MainViewModel only if extending AndroidViewModel

    // Empty constructor (for most ViewModels) extending ViewModel
    public ViewModelFactory() {}

    // Constructor for AuthViewModel and MainViewModel only if extending AndroidViewModel
    public ViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MapViewViewModel.class)) {
            return (T) new MapViewViewModel();
        } else if(modelClass.isAssignableFrom(ListViewViewModel.class)) {
            return (T) new ListViewViewModel();
        } else if(modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel();
        } else if(modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance(),
                    LikedRestaurantRepository.getInstance(), Utils.getInstance());
        } else if(modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance());
        } else if(modelClass.isAssignableFrom(AuthViewModel.class)) {
            // return (T) new AuthViewModel(application); // If extending AndroidViewModel
            return (T) new AuthViewModel(); // If extending ViewModel
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            // return (T) new MainViewModel(application); // If extending AndroidViewModel
            return (T) new MainViewModel(); // If extending ViewModel
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }

}
