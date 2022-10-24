package com.example.go4lunch.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;

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
        //
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //

    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);
        //Set onClickListener to selection fab
        result.findViewById(R.id.selection_fab).setOnClickListener(this);

        return result;
    }

    @Override
    public void onClick(View v) {
        // Spread the click to the parent activity
        mCallback.onButtonClicked(v);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Call the method creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }


    // Declare an interface that will be implemented by any container activity for callback
    public interface OnButtonClickedListener {
        public void onButtonClicked(View view);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }




}