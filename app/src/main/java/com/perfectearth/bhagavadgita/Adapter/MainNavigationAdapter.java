package com.perfectearth.bhagavadgita.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.perfectearth.bhagavadgita.Fragment.AboutFragment;
import com.perfectearth.bhagavadgita.Fragment.BookmarksFragment;
import com.perfectearth.bhagavadgita.Fragment.HomeFragment;
import com.perfectearth.bhagavadgita.Fragment.QuotesFragment;

public class MainNavigationAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;

    public MainNavigationAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new QuotesFragment();
            case 2:
                return new BookmarksFragment();
            case 3:
                return new AboutFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
