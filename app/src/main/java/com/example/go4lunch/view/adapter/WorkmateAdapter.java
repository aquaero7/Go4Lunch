package com.example.go4lunch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.view.viewholder.WorkmateViewHolder;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    private List<User> workmatesList;
    private List<RestaurantWithDistance> restaurantsList;
    private final String CHOICE_TEXT;

    // Constructor
    public WorkmateAdapter(List<User> workmatesList, List<RestaurantWithDistance> restaurantsList, String CHOICE_TEXT) {
        this.workmatesList = workmatesList;
        this.restaurantsList = restaurantsList;
        this.CHOICE_TEXT = CHOICE_TEXT;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmate_list_item, parent, false);

        return new WorkmateViewHolder(view);

    }

    // Update view holder with workmates
    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder viewHolder, int position) {
        viewHolder.updateWithWorkmate(this.workmatesList.get(position), restaurantsList,this.CHOICE_TEXT);
    }

    // Return the total count of workmates
    @Override
    public int getItemCount() {
        return this.workmatesList.size();
    }
}