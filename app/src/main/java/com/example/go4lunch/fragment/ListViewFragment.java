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
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.LikedRestaurant;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utilsforviews.EventListener;
import com.example.go4lunch.utilsforviews.ItemClickSupport;
import com.example.go4lunch.view.ListViewAdapter;
import com.example.go4lunch.viewmodel.LikedRestaurantViewModel;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;
    private LatLng home;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListWithDistances = new ArrayList<>();
    private List<RestaurantWithDistance> filteredRestaurantsListWithDistances = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListToDisplay = new ArrayList<>();
    private List<LikedRestaurant> likedRestaurantsList = new ArrayList<>();
    private List<User> workmatesList = new ArrayList<>();
    private boolean filterIsOn = false;
    private LocationViewModel locationViewModel;
    private RestaurantViewModel restaurantViewModel;
    private LikedRestaurantViewModel likedRestaurantViewModel;
    private UserViewModel userViewModel;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        mRecyclerView = binding.rvListView;

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
        requireActivity().setTitle(R.string.listView_toolbar_title);
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
        listViewAdapter = new ListViewAdapter(restaurantsListToDisplay, workmatesList, getString(R.string.MAPS_API_KEY),
                getString(R.string.status_open), getString(R.string.status_closed), getString(R.string.status_open247),
                getString(R.string.status_open24), getString(R.string.status_open_until),
                getString(R.string.status_open_at), getString(R.string.status_unknown));
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

    private void initViewModels() {
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        likedRestaurantViewModel = new ViewModelProvider(requireActivity()).get(LikedRestaurantViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // TODO : owner =  getViewLifecycleOwner() or requireActivity()
        // Initialize location data
        locationViewModel.getMutableLiveData().observe(requireActivity(), latLng -> {
            home = latLng;
            // Initialize restaurants data
            restaurantViewModel.getMutableLiveData().observe(requireActivity(), restaurants -> {
                restaurantsList.clear();
                restaurantsList.addAll(restaurants);
                restaurantsListWithDistances.clear();
                restaurantsListWithDistances = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
                DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistances);
                if (!filterIsOn) {
                    restaurantsListToDisplay.clear();
                    restaurantsListToDisplay.addAll(restaurantsListWithDistances);
                    listViewAdapter.notifyDataSetChanged();
                }
            });
        });
        // Initialize workmates data
        userViewModel.getMutableLiveData().observe(requireActivity(), workmates -> {
            workmatesList.clear();
            workmatesList.addAll(workmates);
            listViewAdapter.notifyDataSetChanged();
        });
        // Initialize liked restaurants data
        likedRestaurantViewModel.getMutableLiveData().observe(requireActivity(), likedRestaurants -> {
            likedRestaurantsList.clear();
            likedRestaurantsList.addAll(likedRestaurants);
        });
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

    // List filter launched from activity
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(String query) {
        restaurantsListToDisplay.clear();
        filteredRestaurantsListWithDistances.clear();
        if (query.isEmpty()) {
            // SearchView is cleared and closed
            restaurantsListToDisplay.addAll(restaurantsListWithDistances);
            filterIsOn = false;
        } else {
            // A query is sent from searchView
            for (RestaurantWithDistance restaurant : restaurantsListWithDistances) {
                // Switching both strings to lower case to make case insensitive comparison
                if (restaurant.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                    filteredRestaurantsListWithDistances.add(restaurant);
            }
            restaurantsListToDisplay.addAll(filteredRestaurantsListWithDistances);
            filterIsOn = true;
            if (filteredRestaurantsListWithDistances.isEmpty()) showSnackBar(getString(R.string.info_restaurant_not_found));
        }
        // Update recyclerView
        listViewAdapter.notifyDataSetChanged();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}