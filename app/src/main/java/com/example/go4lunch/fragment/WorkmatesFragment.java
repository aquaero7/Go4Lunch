package com.example.go4lunch.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.MainActivity;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.example.go4lunch.utils.EventListener;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.view.WorkmateAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkmatesFragment extends Fragment {

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

    private List<User> workmatesList;
    private User workmateToAdd;

    private EventListener eventListener;


    // Constructor
    public WorkmatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkmatesFragment.
     */
    /*  // To delete
    // TODO: Rename and change types and number of parameters
    public static WorkmatesFragment newInstance(String param1, String param2) {
        WorkmatesFragment fragment = new WorkmatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    // Factory method to create a new instance of this fragment
    public static WorkmatesFragment newInstance() {
        return (new WorkmatesFragment());
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
        FragmentWorkmatesBinding binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        mRecyclerView = binding.rvWorkmates;

        /** To use if menu is handled in fragment
         * Works with onCreateOptionsMenu() and onOptionsItemSelected() */
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            eventListener = (EventListener) context;
        } else {
            Log.w("MapViewFragment", "EventListener error");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup toolbar title (Activity title)
        requireActivity().setTitle(R.string.workmates_toolbar_title);

        getWorkmatesListAndConfigureRecyclerView();
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
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                Toast.makeText(requireContext(), "Click on search button in Workmates", Toast.LENGTH_SHORT).show();   // TODO : To be deleted
                eventListener.toggleSearchViewVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Declare and create adapter
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(workmatesList, getString(R.string.choice_text), getString(R.string.no_choice_text));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(workmateAdapter);
        // 3.4 - Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Configure item click on RecyclerView // Unused
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.workmate_list_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.w("TAG", "Position : "+position);
                    }
                });
    }

    private void getWorkmatesListAndConfigureRecyclerView() {
        workmatesList = FirestoreUtils.getWorkmatesList();
        DataProcessingUtils.sortByName(workmatesList);
        configureRecyclerView();
        configureOnClickRecyclerView();
    }


}