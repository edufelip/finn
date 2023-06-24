package com.projects.finn.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.projects.finn.ui.activities.profileFragments.PostsFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentsAdapter extends FragmentStateAdapter {
    public List<Fragment> fragments = new ArrayList<>();

    public FragmentsAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        Fragment postsFragment = new PostsFragment();
        fragments.add(postsFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return fragments.get(0);
        }
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
