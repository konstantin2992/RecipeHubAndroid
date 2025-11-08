package com.example.recipehub.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfilePagerAdapter extends FragmentStateAdapter {
    public ProfilePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FavoritesFragment();
        }
        return new MyRecipesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}