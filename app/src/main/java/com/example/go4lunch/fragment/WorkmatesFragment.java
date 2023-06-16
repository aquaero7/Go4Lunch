package com.example.go4lunch.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utilsforviews.ItemClickSupport;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.view.WorkmateAdapter;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;

    // Declare RecyclerView
    private RecyclerView mRecyclerView;

    private List<User> workmatesList = new ArrayList<>();
    private List<Restaurant> restaurantsList = new ArrayList<>();

    private UserViewModel userViewModel;
    private LocationViewModel locationViewModel;
    private RestaurantViewModel restaurantViewModel;
    private LatLng home;

    private EventListener eventListener;


    // Constructor
    public WorkmatesFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment
    public static WorkmatesFragment newInstance() {
        return (new WorkmatesFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        mRecyclerView = binding.rvWorkmates;

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        // Initialize ViewModels    // TODO : Test MVVM
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        // userViewModel.fetchWorkmates();  // TODO : To be deleted
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        // locationViewModel.fetchLocation(requireActivity());  // TODO : To be deleted
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        // restaurantViewModel.fetchRestaurants(requireActivity(), getString(R.string.MAPS_API_KEY));   // TODO : To be deleted

        // Initialize RecyclerView  // TODO : Test MVVM
        configureRecyclerView();
        configureOnClickRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            eventListener = (EventListener) context;
        } else {
            Log.w("MapViewFragment", "EventListener error");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.workmates_toolbar_title);
        // Getting workmates list from Firestore and configure RecyclerView
        // getWorkmatesListAndConfigureRecyclerView(); // TODO : Test MVVM
        // Update data
        updateData();   // TODO : Test MVVM
    }

    /** To use with setHasOptionsMenu(true), if menu is handled in fragment */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main_menu, menu);
    }

    /** To use with setHasOptionsMenu(true), if menu is handled in fragment */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                // Toast.makeText(requireContext(), "Click on search button in Workmates", Toast.LENGTH_SHORT).show();   // TODO : To be deleted
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(workmatesList, getString(R.string.text_choice), getString(R.string.text_no_choice));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(workmateAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.workmate_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Log.w("TAG", "Position : "+position);   // TODO : To be deleted
                    if (workmatesList.size() != 0) {
                        // Get workmate
                        User workmate = workmatesList.get(position);
                        // Get workmate selection
                        String rId = workmate.getSelectionId();
                        String selectionDate = workmate.getSelectionDate();
                        String currentDate = CalendarUtils.getCurrentDate();
                        // If a restaurant is selected, get it from restaurants list and launch detail activity
                        if (rId != null && currentDate.equals(selectionDate)) {
                            RestaurantWithDistance restaurantWithDistance = getSelectedRestaurant(rId);
                            if (restaurantWithDistance != null) {
                                launchDetailRestaurantActivity(restaurantWithDistance);
                            } else {
                                Snackbar.make(binding.getRoot(), getString(R.string.error_no_selection), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(binding.getRoot(), getString(R.string.error_no_selection), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*  // TODO : Test MVVM
    private void getWorkmatesListAndConfigureRecyclerView() {
        workmatesList = FirestoreUtils.getWorkmatesList();
        // workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();   // TODO : May not keep coherence between fragments
        DataProcessingUtils.sortByName(workmatesList);
        configureRecyclerView();
        configureOnClickRecyclerView();
    }
    */
    // TODO : Test MVVM
    private void updateData() {
        // Initialize location data
        locationViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), latLng -> {
            home = latLng;
        });
        // Initialize restaurants data
        restaurantViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            restaurantsList.clear();
            restaurantsList.addAll(restaurants);
        });
        // Initialize workmates data
        userViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
            DataProcessingUtils.sortByName(workmatesList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    private RestaurantWithDistance getSelectedRestaurant(String rId) {
        RestaurantWithDistance selectedRestaurant = null;
        for (Restaurant restaurant : restaurantsList) {
            if (rId.equals(restaurant.getRid())) {
                int distance = (home != null) ?
                        DataProcessingUtils.calculateRestaurantDistance(restaurant, home) : 0;

                selectedRestaurant = new RestaurantWithDistance(
                        restaurant.getRid(), restaurant.getName(),
                        restaurant.getPhotos(), restaurant.getAddress(),
                        restaurant.getRating(), restaurant.getOpeningHours(),
                        restaurant.getPhoneNumber(), restaurant.getWebsite(),
                        restaurant.getGeometry(), distance);
                break;
            }
        }
        return selectedRestaurant;
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurantWithDistance) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurantWithDistance);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}