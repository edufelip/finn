package com.projects.finn.ui.activities.homeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.projects.finn.R;
import com.projects.finn.databinding.FragmentNotificationsBinding;


public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
