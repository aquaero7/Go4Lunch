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
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.utils.EventObjectClick;
import com.example.go4lunch.view.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.adapter.WorkmateAdapter;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.example.go4lunch.viewmodel.WorkmatesViewModel;
import com.google.android.material.snackbar.Snackbar;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private EventListener eventListener;
    private WorkmateAdapter workmateAdapter;
    private WorkmatesViewModel workmatesViewModel;

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
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        workmatesViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory()).get(WorkmatesViewModel.class);

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
        workmateAdapter = new WorkmateAdapter(workmatesViewModel.getWorkmates(), getString(R.string.text_choice));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        binding.rvWorkmates.setAdapter(workmateAdapter);
        // 3.4 - Set layout manager to position the items
        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(binding.rvWorkmates, R.layout.workmate_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    if (workmatesViewModel.getWorkmates().size() != 0) {
                        // Get workmate selection
                        Restaurant restaurant = workmatesViewModel.getWorkmateSelection(position);
                        // Launch DetailActivity or show message
                        if (restaurant != null) {
                            launchDetailRestaurantActivity(restaurant);
                        } else {
                            Snackbar.make(binding.getRoot(), getString(R.string.error_no_selection), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        // Initialize workmates data
        workmatesViewModel.getWorkmatesMutableLiveData().observe(requireActivity(), workmates -> {
            workmateAdapter.notifyDataSetChanged();
        });
    }

    private void launchDetailRestaurantActivity(Restaurant restaurant) {
        Intent intent = new Intent(requireActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}