package com.example.go4lunch.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utilsforviews.ItemClickSupport;
import com.example.go4lunch.view.WorkmateAdapter;
import com.example.go4lunch.viewmodel.LikedRestaurantViewModel;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private RecyclerView mRecyclerView;
    private WorkmateAdapter workmateAdapter;
    private List<User> workmatesList = new ArrayList<>();
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
    private UserViewModel userViewModel;
    private LocationViewModel locationViewModel;
    private RestaurantViewModel restaurantViewModel;
    private LikedRestaurantViewModel likedRestaurantViewModel;
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

        // Initialize ViewModels
        initViewModels();

        // Initialize RecyclerView
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
        // Initialize data
        initData();
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
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter
        workmateAdapter = new WorkmateAdapter(workmatesList, getString(R.string.text_choice), getString(R.string.text_no_choice));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(workmateAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.workmate_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
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

    private void initViewModels() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        likedRestaurantViewModel = new ViewModelProvider(requireActivity()).get(LikedRestaurantViewModel.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // TODO : owner =  getViewLifecycleOwner() or requireActivity()
        // Initialize location data
        locationViewModel.getMutableLiveData().observe(requireActivity(), latLng -> {
            home = latLng;
        });
        // Initialize restaurants data
        restaurantViewModel.getMutableLiveData().observe(requireActivity(), restaurants -> {
            restaurantsList.clear();
            restaurantsList.addAll(restaurants);
        });
        // Initialize workmates data
        userViewModel.getMutableLiveData().observe(requireActivity(), workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
            DataProcessingUtils.sortByName(workmatesList);
            workmateAdapter.notifyDataSetChanged();
        });
        // Initialize liked restaurants data
        likedRestaurantViewModel.getMutableLiveData().observe(requireActivity(), likedRestaurants -> {
            likedRestaurantsList.clear();
            likedRestaurantsList.addAll(likedRestaurants);
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
        bundle.putSerializable("LIKED_RESTAURANTS", (Serializable) likedRestaurantsList);
        bundle.putSerializable("WORKMATES", (Serializable) workmatesList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}