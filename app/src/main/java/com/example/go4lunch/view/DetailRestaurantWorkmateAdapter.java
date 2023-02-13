package com.example.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.List;

public class DetailRestaurantWorkmateAdapter extends RecyclerView.Adapter<DetailRestaurantWorkmateViewHolder> {

    private List<User> selectorsList;
    private final String JOINING_TEXT;

    // Constructor
    public DetailRestaurantWorkmateAdapter(List<User> selectorsList, String JOINING_TEXT) {
        this.selectorsList = selectorsList;
        this.JOINING_TEXT = JOINING_TEXT;
    }

    @NonNull
    @Override
    public DetailRestaurantWorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmate_list_item, parent, false);

        return new DetailRestaurantWorkmateViewHolder(view);
    }

    // Update view holder with workmates who have selected this restaurant
    @Override
    public void onBindViewHolder(@NonNull DetailRestaurantWorkmateViewHolder viewHolder, int position) {
        viewHolder.updateWithSelector(this.selectorsList.get(position), this.JOINING_TEXT);

    }

    // Return the total count of workmates who have selected this restaurant
    @Override
    public int getItemCount() {
        return this.selectorsList.size();
    }


}
