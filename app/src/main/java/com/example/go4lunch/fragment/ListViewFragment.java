package com.example.go4lunch.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.manager.RestaurantManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.Geometry;
import com.example.go4lunch.model.api.OpeningHours;
import com.example.go4lunch.model.api.Photo;
import com.example.go4lunch.view.ListViewAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

    // Declare RecyclerView
    private RecyclerView mRecyclerView;

    private Restaurant restaurantFromData;
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
        FragmentListViewBinding binding = FragmentListViewBinding.inflate(inflater, container, false);

        mRecyclerView = binding.rvListView;
        configureRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.listView_toolbar_title);
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter (TODO : Pass the list of restaurants)
        List<Restaurant> restaurantsList = getRestaurantsList();
        ListViewAdapter listViewAdapter = new ListViewAdapter(restaurantsList, getString(R.string.MAPS_API_KEY));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(listViewAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private List<Restaurant> getRestaurantsList() {
        List<Restaurant> restaurantsList = new ArrayList<>();
        RestaurantManager.getRestaurantsList(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    // Map<String, Object> myData = restaurant.getData();                          //
                    // Restaurant myRestaurant = restaurant.toObject(Restaurant.class);            //

                    restaurantToAdd = getRestaurantData(documentId);
                    if (restaurantToAdd != null) {
                        restaurantsList.add(restaurantToAdd);
                    }
                }
            } else {
                Log.d("ListViewFragment", "Error getting documents: ", task.getException());
            }

        });
        return restaurantsList;

    }

    private Restaurant getRestaurantData(String id){
        RestaurantManager.getRestaurantData(id).addOnSuccessListener(restaurant -> {
            String rId = restaurant.getId();
            String rName = restaurant.getName();
            int rDistance = restaurant.getDistance();
            List<Photo> rPhoto = restaurant.getPhotos();
            String rNationality = restaurant.getNationality();
            String rAddress = restaurant.getAddress();
            double rRating = restaurant.getRating();
            OpeningHours rOpeningHours = restaurant.getOpeningHours();
            int rLikesCount = restaurant.getLikesCount();
            String rPhoneNumber = restaurant.getPhoneNumber();
            String rWebsite = restaurant.getWebsite();
            Geometry rGeometry = restaurant.getGeometry();
            List<User> rSelectors = restaurant.getSelectors();

            restaurantFromData = new Restaurant(rId, rName, rDistance, rPhoto, rNationality, rAddress,
                    rRating, rOpeningHours, rLikesCount, rPhoneNumber, rWebsite, rGeometry, rSelectors);


        });

        return restaurantFromData;
    }

}