package com.example.go4lunch.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.view.viewholder.WorkmateViewHolder;
import com.example.go4lunch.viewmodel.WorkmatesViewModel;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    private final List<User> workmatesList;
    private final String choiceText;

    // Constructor
    public WorkmateAdapter(List<User> workmatesList, String choiceText) {
        this.workmatesList = workmatesList;
        this.choiceText = choiceText;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder and inflate its layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workmate_list_item, parent, false);

        return new WorkmateViewHolder(view);
    }

    // Update view holder with workmates
    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder viewHolder, int position) {
        User workmate = workmatesList.get(position);

        /*
        String textAndChoice = (workmate.getSelectionName() != null && workmate.getSelectionDate() != null
                && Objects.equals(Utils.getCurrentDate(), workmate.getSelectionDate())) ?
                choiceText + workmate.getSelectionName() : "";
        */
        String textAndChoice = new WorkmatesViewModel().getTextAndChoice(choiceText, workmate);

        viewHolder.updateWithWorkmate(workmate.getUserUrlPicture() , workmate.getUsername(), textAndChoice);
    }

    // Return the total count of workmates
    @Override
    public int getItemCount() {
        return this.workmatesList.size();
    }
}
