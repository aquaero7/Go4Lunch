package com.example.go4lunch.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.view.viewholder.DetailRestaurantWorkmateViewHolder;

import java.util.List;

public class DetailRestaurantWorkmateAdapter extends RecyclerView.Adapter<DetailRestaurantWorkmateViewHolder> {

    private final List<User> selectorsList;
    private final String selectorText;

    // Constructor
    public DetailRestaurantWorkmateAdapter(List<User> selectorsList, String selectorText) {
        this.selectorsList = selectorsList;
        this.selectorText = selectorText;
    }

    @NonNull
    @Override
    public DetailRestaurantWorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workmate_list_item, parent, false);

        return new DetailRestaurantWorkmateViewHolder(view);
    }

    // Update view holder with workmates who have selected this restaurant
    @Override
    public void onBindViewHolder(@NonNull DetailRestaurantWorkmateViewHolder viewHolder, int position) {
        User selector = selectorsList.get(position);
        viewHolder.updateWithSelector(selector.getUserUrlPicture(),selector.getUsername() + selectorText);
    }

    // Return the total count of workmates who have selected this restaurant
    @Override
    public int getItemCount() {
        return this.selectorsList.size();
    }

}
