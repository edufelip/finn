package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.projects.finn.R;
import com.projects.finn.adapters.FragmentsAdapter;
import com.google.android.material.tabs.TabLayout;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 pager;
    private FragmentsAdapter pagerAdapter;
    private ViewPager2.OnPageChangeCallback callback;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        setClickListeners();
        setupViewPager();
        pager.registerOnPageChangeCallback(callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pager.unregisterOnPageChangeCallback(callback);
    }

    public void initializeComponents() {
        tabLayout = findViewById(R.id.profile_tab_layout);
        pager = findViewById(R.id.profile_view_pager2);
        callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        };
        backButton = findViewById(R.id.profile_back_button);
    }

    public void setClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    public void setupViewPager() {
        FragmentManager fm = getSupportFragmentManager();
        pagerAdapter = new FragmentsAdapter(fm, getLifecycle());
        pager.setAdapter(pagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Comments"));
        tabLayout.addTab(tabLayout.newTab().setText("Likes"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
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