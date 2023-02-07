package com.example.go4lunch.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.DetailRestaurantActivity;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.utils.MapsApisUtils;
import com.example.go4lunch.view.ListViewAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment {

    /*  // To delete
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    */

    private FragmentListViewBinding binding;

    // Declare RecyclerView
    private RecyclerView mRecyclerView;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng home;
    private List<Restaurant> restaurantsList;
    private List<Restaurant> restaurantsListForDisplay;
    private Restaurant restaurantToAdd;


    // Constructor
    public ListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListViewFragment.
     */
    /*  // To delete
    // TODO: Rename and change types and number of parameters
    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    // Factory method to create a new instance of this fragment
    public static ListViewFragment newInstance() {
        return (new ListViewFragment());
    }

    /*  // To delete ?
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        mRecyclerView = binding.rvListView;

        // Create a new FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.listView_toolbar_title);

        /** Solution A : Getting data from API in MapsApisUtils */
        //
        home = MapsApisUtils.getDeviceLocation(true, fusedLocationProviderClient, requireActivity());
        restaurantsList = MapsApisUtils.getRestaurantsFromApi(home, getString(R.string.MAPS_API_KEY), requireContext());
        restaurantsListForDisplay = DataProcessingUtils.customizeRestaurantsList(restaurantsList);
        configureRecyclerView();
        //

        /** Solution B : Getting data from Firestore in MapsApisUtils */
        // getRestaurantsListAndConfigureRecyclerView();

        configureOnClickRecyclerView();

    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter
        ListViewAdapter listViewAdapter = new ListViewAdapter(restaurantsListForDisplay, getString(R.string.MAPS_API_KEY),
                getString(R.string.status_open), getString(R.string.status_closed), getString(R.string.status_open247),
                getString(R.string.status_open_until), getString(R.string.status_open_at));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(listViewAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_detail_restaurant)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Log.w("TAG", "Position : "+position);       // TODO : To be deleted
                    if (restaurantsListForDisplay.size() != 0) {
                        Restaurant mRestaurant = restaurantsListForDisplay.get(position);
                        launchDetailRestaurantActivity(mRestaurant);
                    }
                });
    }

    private void getRestaurantsListAndConfigureRecyclerView() {
        restaurantsListForDisplay = new ArrayList<>();
        RestaurantManager.getRestaurantsList(task -> {
            if (task.isSuccessful()) {
                // Get restaurants list
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> restaurantData = document.getData(); // TODO : Map data for debug. To be deleted
                    restaurantToAdd = FirestoreUtils.getRestaurantFromDatabaseDocument(document);
                    restaurantsListForDisplay.add(restaurantToAdd);
                }
            } else {
                Log.d("ListViewFragment", "Error getting documents: ", task.getException());
                Toast.makeText(requireContext(), "Error retrieving restaurant list from database", Toast.LENGTH_SHORT).show();    // TODO : For debug
            }

            configureRecyclerView();

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