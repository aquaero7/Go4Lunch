package com.example.go4lunch.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.utils.EventObjectClick;
import com.example.go4lunch.view.activity.DetailRestaurantActivity;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.adapter.ListViewAdapter;
import com.example.go4lunch.viewmodel.ListViewViewModel;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private EventListener eventListener;
    private ListViewAdapter listViewAdapter;
    private ListViewViewModel listViewViewModel;

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
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        listViewViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(ListViewViewModel.class);

        /** To use if menu is handled in fragment. Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        // Initialize RecyclerView
        configureRecyclerView();
        configureOnClickRecyclerView();
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
        switch (EventObjectClick.fromMenuItem(item)) {
            case MENU_ITEM_SEARCH:
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter
        listViewAdapter = new ListViewAdapter(listViewViewModel.getRestaurantsToDisplay(), listViewViewModel.getWorkmates(), requireContext());
        // 3.3 - Attach the adapter to the recyclerview to populate items
        binding.rvListView.setAdapter(listViewAdapter);
        // 3.4 - Set layout manager to position the items
        binding.rvListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(binding.rvListView, R.layout.restaurant_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    if (listViewViewModel.getRestaurantsToDisplay().size() != 0) {
                        RestaurantWithDistance restaurant = listViewViewModel.getRestaurantsToDisplay().get(position);
                        launchDetailRestaurantActivity(restaurant);
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize workmates data
        listViewViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            listViewAdapter.notifyDataSetChanged();
        });
        // Initialize restaurants data
        listViewViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            listViewViewModel.setRestaurantsToDisplay(restaurants);
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
        // Apply filter and display message if nothing matches query
        if (listViewViewModel.filterList(query, requireContext()) != null)
            Snackbar.make(binding.getRoot(), listViewViewModel.filterList(query, requireContext()), Snackbar.LENGTH_LONG)
                    .show();
        // Update recyclerView
        listViewAdapter.notifyDataSetChanged();
    }

}