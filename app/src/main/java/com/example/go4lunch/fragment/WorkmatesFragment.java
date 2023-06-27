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
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utilsforviews.ItemClickSupport;
import com.example.go4lunch.view.WorkmateAdapter;
import com.example.go4lunch.viewmodel.WorkmatesViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private RecyclerView mRecyclerView;
    private WorkmateAdapter workmateAdapter;
    private User currentUser;
    private List<User> workmatesList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsList = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
    private WorkmatesViewModel workmatesViewModel;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            eventListener = (EventListener) context;
        } else {
            Log.w("WorkmatesFragment", "EventListener error");
        }
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

        // Initialize RecyclerView
        configureRecyclerView();
        configureOnClickRecyclerView();

        // Initialize ViewModel
        workmatesViewModel = new ViewModelProvider(requireActivity()).get(WorkmatesViewModel.class);
        // Initialize data
        initData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.workmates_toolbar_title);
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
        workmateAdapter = new WorkmateAdapter(workmatesList, restaurantsList, getString(R.string.text_choice), getString(R.string.text_no_choice));
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
                        RestaurantWithDistance restaurant = workmatesViewModel.checkWorkmateSelection(workmate, restaurantsList);
                        // Launch DetailActivity or show message
                        if (restaurant != null) {
                            launchDetailRestaurantActivity(restaurant);
                        } else {
                            Snackbar.make(binding.getRoot(), getString(R.string.error_no_selection), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize current user
        workmatesViewModel.getCurrentUserMutableLiveData().observe(requireActivity(), user -> currentUser = user);
        // Initialize workmates data
        workmatesViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
            workmateAdapter.notifyDataSetChanged();
        });
        // Initialize restaurants data
        workmatesViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            restaurantsList.clear();
            restaurantsList.addAll(restaurants);
        });
        // Initialize liked restaurants list
        workmatesViewModel.getLikedRestaurantsMutableLiveData().observe(requireActivity(), likedRestaurants -> {
            likedRestaurantsList.clear();
            likedRestaurantsList.addAll(likedRestaurants);
        });
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurantWithDistance) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurantWithDistance);
        bundle.putSerializable("CURRENT_USER", currentUser);
        bundle.putSerializable("WORKMATES", (Serializable) workmatesList);
        bundle.putSerializable("LIKED_RESTAURANTS", (Serializable) likedRestaurantsList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}