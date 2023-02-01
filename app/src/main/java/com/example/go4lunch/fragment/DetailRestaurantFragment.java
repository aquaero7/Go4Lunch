package com.example.go4lunch.fragment;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentDetailRestaurantBinding;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.DetailRestaurantWorkmateAdapter;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailRestaurantFragment extends Fragment implements View.OnClickListener {

    /* TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    */

    /* TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    */


    // Declare callback
    private OnButtonClickedListener mCallback;

    // Declare ViewBinding
    private FragmentDetailRestaurantBinding binding;

    // Declare View items
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private TextView mTextView1;
    private RatingBar mRatingBar;
    private TextView mTextView2;

    // Declare restaurant
    private Restaurant restaurant;

    // Initialize Google Maps API key
    private String KEY;


    // Constructor
    public DetailRestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailRestaurantFragment.
     */
    /* TODO: Rename and change types and number of parameters
    public static DetailRestaurantFragment newInstance(String param1, String param2) {
        DetailRestaurantFragment fragment = new DetailRestaurantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    public static DetailRestaurantFragment newInstance() {
        return (new DetailRestaurantFragment());
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getIntentData();
        if (restaurant != null) displayRestaurantData();
    }
    */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailRestaurantBinding.inflate(inflater, container, false);

        // Initialize View items
        mRecyclerView = binding.rvDetailRestaurant;
        mImageView = binding.restaurantIv;
        mTextView1 = binding.restaurantTv1;
        mTextView2 = binding.restaurantTv2;
        mRatingBar = binding.restaurantRatingBar;

        // Get restaurant from calling activity
        getIntentData();
        if (restaurant != null) displayRestaurantData();

        //Set onClickListener to selection fab
        binding.selectionFab.setOnClickListener(this);

        // Set onClickListener to buttons
        binding.callButton.setOnClickListener(this);
        binding.likeButton.setOnClickListener(this);
        binding.websiteButton.setOnClickListener(this);

        configureRecyclerView();
        configureOnClickRecyclerView();

        return binding.getRoot();
    }

    @Override
    /* Spread the click to the parent activity
    Binding added as an argument to make it available in the activity
    */
    public void onClick(View v) {
        // mCallback.onButtonClicked(v);    // TODO : to be deleted
        mCallback.onButtonClicked(v, binding);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
        KEY  = getString(R.string.MAPS_API_KEY);
    }


    /*
    Declare an interface that will be implemented by any container activity for callback
    Binding added as an argument to make it available in the activity
    */
    public interface OnButtonClickedListener {
        // public void onButtonClicked(View view);  // TODO : to be deleted
        void onButtonClicked(View view, FragmentDetailRestaurantBinding binding);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e + " must implement OnButtonClickedListener");
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter (TODO : Pass the list of workmates)
        DetailRestaurantWorkmateAdapter detailRestaurantWorkmateAdapter = new DetailRestaurantWorkmateAdapter();
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(detailRestaurantWorkmateAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_detail_restaurant)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.w("TAG", "Position : "+position);
                    }
                });
    }

    // Get restaurant from calling activity
    private void getIntentData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurant = (Restaurant) bundle.getSerializable("RESTAURANT");
                Log.w("DetailRestaurantFragment", "Name of this restaurant : " + restaurant.getName());    // TODO : For debug. To be deleted
                Toast.makeText(requireContext(), restaurant.getName(), Toast.LENGTH_SHORT).show();  // TODO : For debug. To be deleted
            }
        }
    }

    private void displayRestaurantData() {
        if (restaurant.getPhotos() != null) Picasso.get().load(restaurant.getPhotos().get(0).getPhotoUrl(KEY)).into(mImageView);
        mTextView1.setText(restaurant.getName());
        mTextView2.setText(restaurant.getAddress());
        mRatingBar.setRating((float) (restaurant.getRating() * 3/5));
    }



}