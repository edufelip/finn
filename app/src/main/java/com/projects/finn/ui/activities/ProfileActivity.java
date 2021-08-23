package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.projects.finn.adapters.FragmentsAdapter;
import com.projects.finn.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FragmentsAdapter pagerAdapter;
    private ViewPager2.OnPageChangeCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        
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

    public void initializeComponents() {
        callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.profileTabLayout.selectTab(binding.profileTabLayout.getTabAt(position));
            }
        };
    }

    public void setClickListeners() {
        binding.profileBackButton.setOnClickListener(v -> finish());
    }

    public void setupViewPager() {
        FragmentManager fm = getSupportFragmentManager();
        pagerAdapter = new FragmentsAdapter(fm, getLifecycle());
        binding.profileViewPager2.setAdapter(pagerAdapter);

        binding.profileTabLayout.addTab(binding.profileTabLayout.newTab().setText("Posts"));
        binding.profileTabLayout.addTab(binding.profileTabLayout.newTab().setText("Comments"));
        binding.profileTabLayout.addTab(binding.profileTabLayout.newTab().setText("Likes"));
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