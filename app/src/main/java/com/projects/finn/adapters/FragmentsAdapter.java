package com.projects.finn.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.projects.finn.ui.activities.profileFragments.CommentsFragment;
import com.projects.finn.ui.activities.profileFragments.LikesFragment;
import com.projects.finn.ui.activities.profileFragments.PostsFragment;

public class FragmentsAdapter extends FragmentStateAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PostsFragment();
            case 1:
                return new CommentsFragment();
        }
        // case 2
        return new LikesFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
