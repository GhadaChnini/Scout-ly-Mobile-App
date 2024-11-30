package com.example.myapplication.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.myapplication.Fragments.CartFragment;
import com.example.myapplication.Fragments.EditAccountFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.Fragments.SearchFragment;
import com.example.myapplication.R;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public FragmentAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new CartFragment();
            case 3:
                return new EditAccountFragment(); // Return the new Fragment
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4; // Update the count
    }

    // Adding icons for each tab
    public int getTabIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_home; // Home icon
            case 1:
                return R.drawable.ic_search; // Search icon
            case 2:
                return R.drawable.ic_cart; // Cart icon
            case 3:
                return R.drawable.ic_profile; // Profile icon
            default:
                return 0;
        }
    }
}
