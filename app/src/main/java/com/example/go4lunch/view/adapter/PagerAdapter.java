package com.example.go4lunch.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.go4lunch.view.fragment.ListViewFragment;
import com.example.go4lunch.view.fragment.MapViewFragment;
import com.example.go4lunch.view.fragment.WorkmatesFragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentManager mgr, @NonNull Lifecycle lifecycle) {
        super(mgr, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Page to return
        switch (position){
            case 0: // Page number 1
                return MapViewFragment.newInstance();
            case 1: // Page number 2
                return ListViewFragment.newInstance();
            case 2: // Page number 3
                return WorkmatesFragment.newInstance();
            // default:    //Page number 1
            //     return MapViewFragment.newInstance();
        }

        return MapViewFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
