package com.example.go4lunch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.model.repository.AutocompleteRepository;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.utils.Utils;

public class ViewModelFactory implements ViewModelProvider.Factory {

    // Constructor
    public ViewModelFactory() {}

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MapViewViewModel.class)) {
            return (T) new MapViewViewModel(
                    UserRepository.getInstance(), LocationRepository.getInstance(),
                    RestaurantRepository.getInstance(), AutocompleteRepository.getInstance(),
                    Utils.getInstance());

        } else if(modelClass.isAssignableFrom(ListViewViewModel.class)) {
            return (T) new ListViewViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance(),
                    Utils.getInstance());

        } else if(modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance(),
                    Utils.getInstance());

        } else if(modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance(),
                    LikedRestaurantRepository.getInstance(), Utils.getInstance());

        } else if(modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(
                    UserRepository.getInstance(), RestaurantRepository.getInstance());

        } else if(modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(
                    UserRepository.getInstance(), LocationRepository.getInstance(),
                    RestaurantRepository.getInstance(), LikedRestaurantRepository.getInstance());

        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(
                    UserRepository.getInstance(), LocationRepository.getInstance(),
                    RestaurantRepository.getInstance(), LikedRestaurantRepository.getInstance(),
                    Utils.getInstance());

        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }

}
