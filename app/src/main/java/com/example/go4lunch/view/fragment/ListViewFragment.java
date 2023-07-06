package com.example.go4lunch.view.fragment;

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
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.view.activity.DetailRestaurantActivity;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.adapter.ListViewAdapter;
import com.example.go4lunch.viewmodel.ListViewViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;
    private List<User> workmatesList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsList = new ArrayList<>();
    private List<RestaurantWithDistance> filteredRestaurantsList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListToDisplay = new ArrayList<>();
    private boolean filterIsOn = false;
    private ListViewViewModel listViewViewModel;
    private EventListener eventListener;

    // Constructor
    public ListViewFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment
    public static ListViewFragment newInstance() {
        return (new ListViewFragment());
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            eventListener = (EventListener) context;
        } else {
            Log.w("ListViewFragment", "EventListener error");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        mRecyclerView = binding.rvListView;

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        // Initialize RecyclerView
        configureRecyclerView();
        configureOnClickRecyclerView();

        // Initialize ViewModel
        listViewViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(ListViewViewModel.class);

        // Initialize data
        initData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.listView_toolbar_title);
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
        listViewAdapter = new ListViewAdapter(restaurantsListToDisplay, workmatesList);
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(listViewAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.restaurant_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    if (restaurantsListToDisplay.size() != 0) {
                        RestaurantWithDistance mRestaurantWithDistance = restaurantsListToDisplay.get(position);
                        launchDetailRestaurantActivity(mRestaurantWithDistance);
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize workmates data
        listViewViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
            listViewAdapter.notifyDataSetChanged();
        });
        // Initialize restaurants data
        listViewViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            restaurantsList.clear();
            restaurantsList.addAll(restaurants);
            if (!filterIsOn) {
                restaurantsListToDisplay.clear();
                restaurantsListToDisplay.addAll(restaurantsList);
            }
            listViewAdapter.notifyDataSetChanged();
        });
    }

    private void launchDetailRestaurantActivity(RestaurantWithDistance restaurantWithDistance) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurantWithDistance);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // List filter launched from activity
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(String query) {
        restaurantsListToDisplay.clear();
        filteredRestaurantsList.clear();
        if (query.isEmpty()) {
            // SearchView is cleared and closed
            restaurantsListToDisplay.addAll(restaurantsList);
            filterIsOn = false;
        } else {
            // A query is sent from searchView
            for (RestaurantWithDistance restaurant : restaurantsList) {
                // Switching both strings to lower case to make case insensitive comparison
                if (restaurant.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                    filteredRestaurantsList.add(restaurant);
            }
            restaurantsListToDisplay.addAll(filteredRestaurantsList);
            filterIsOn = true;
            if (filteredRestaurantsList.isEmpty()) showSnackBar(getString(R.string.info_restaurant_not_found));
        }
        // Update recyclerView
        listViewAdapter.notifyDataSetChanged();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}