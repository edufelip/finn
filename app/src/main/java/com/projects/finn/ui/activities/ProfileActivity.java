package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;

import com.bumptech.glide.RequestManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.R;
import com.projects.finn.ui.adapters.FragmentsAdapter;
import com.projects.finn.databinding.ActivityProfileBinding;
import com.projects.finn.ui.viewmodels.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileActivity extends AppCompatActivity {
    @Inject
    RequestManager glide;
    @Inject
    FirebaseAuth auth;
    private ActivityProfileBinding binding;
    private FragmentsAdapter pagerAdapter;
    private ViewPager2.OnPageChangeCallback callback;
    private ProfileViewModel mProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        initializeViewModel();
        initializeComponents();
        setClickListeners();
        setupViewPager();
        binding.profileViewPager2.registerOnPageChangeCallback(callback);

        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.profileViewPager2.unregisterOnPageChangeCallback(callback);
    }

    public void initializeViewModel() {
        String id = auth.getCurrentUser().getUid();

        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        mProfileViewModel.getUser(id);

        mProfileViewModel.observeUser().observe(this, user -> {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
            String date = DATE_FORMAT.format(user.getDate());
            String capsDate = date.substring(0, 1).toUpperCase() + date.substring(1);
            String message = getResources().getString(R.string.joined_since) + " " + capsDate;
            binding.userDate.setText(message);
        });
    }

    public void initializeComponents() {
        callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.profileTabLayout.selectTab(binding.profileTabLayout.getTabAt(position));
            }
        };

        binding.profileUserName.setText(auth.getCurrentUser().getDisplayName());
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.profilePicture);
    }

    public void setClickListeners() {
        binding.profileBackButton.setOnClickListener(v -> finish());
    }

    public void setupViewPager() {
        FragmentManager fm = getSupportFragmentManager();
        pagerAdapter = new FragmentsAdapter(fm, getLifecycle());
        binding.profileViewPager2.setAdapter(pagerAdapter);

        binding.profileTabLayout.addTab(binding.profileTabLayout.newTab().setText("Posts"));
        binding.profileTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.profileViewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}