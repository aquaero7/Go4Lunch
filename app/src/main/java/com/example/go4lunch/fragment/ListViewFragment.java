package com.example.go4lunch.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantWithDistance;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.view.ListViewAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;

    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;

    // private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng home;
    private List<Restaurant> restaurantsList;
    private List<RestaurantWithDistance> restaurantsListWithDistances;
    private List<RestaurantWithDistance> filteredRestaurantsListWithDistances = new ArrayList<>();
    private List<RestaurantWithDistance> restaurantsListToDisplay = new ArrayList<>();
    private boolean filterIsOn = false;

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

        // Create a new FusedLocationProviderClient.
        // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

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
        // Getting restaurants list from Firestore and configure RecyclerView
        getRestaurantsListAndConfigureRecyclerView();
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
        listViewAdapter = new ListViewAdapter(restaurantsListToDisplay, getString(R.string.MAPS_API_KEY),
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

    private void getRestaurantsListAndConfigureRecyclerView() {
        home = MapsApisUtils.getHome();
        restaurantsList = FirestoreUtils.getRestaurantsList();
        // restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();   // TODO : May not keep coherence between fragments
        restaurantsListWithDistances = DataProcessingUtils.updateRestaurantsListWithDistances(restaurantsList, home);
        DataProcessingUtils.sortByDistanceAndName(restaurantsListWithDistances);
        if (!filterIsOn) restaurantsListToDisplay.addAll(restaurantsListWithDistances);
        configureRecyclerView();
        configureOnClickRecyclerView();
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
            if (filteredRestaurantsListWithDistances.isEmpty()) showSnackBar(getString(R.string.not_found_error));
        }
        // Update recyclerView
        listViewAdapter.notifyDataSetChanged();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}