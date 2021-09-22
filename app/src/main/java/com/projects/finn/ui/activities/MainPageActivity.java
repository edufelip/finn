package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import com.bumptech.glide.RequestManager;
import com.projects.finn.R;
import com.projects.finn.databinding.ActivityMainPageBinding;
import com.projects.finn.databinding.NavHeaderBinding;
import com.projects.finn.ui.activities.homeFragments.SearchFragment;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.ui.activities.homeFragments.HomeFragment;
import com.projects.finn.ui.activities.homeFragments.NotificationsFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.utils.Authentication;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainPageActivity extends AppCompatActivity implements HandleClick {
    @Inject
    FirebaseAuth auth;
    @Inject
    RequestManager glide;
    public static Activity mainPageActivity;
    private ActivityMainPageBinding binding;
    private ActionBarDrawerToggle toggle;
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private NotificationsFragment notificationsFragment;
    private Fragment activeFragment;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        mainPageActivity = this;

        initializeComponents();
        setupBottomNavigationView();
        setupNavigationDrawer();
        updateNavUserInfo();
        setupClickListeners();
        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        homeFragment = new HomeFragment();
        homeFragment.setInterface(MainPageActivity.this);
        searchFragment = new SearchFragment();
        searchFragment.setInterface(MainPageActivity.this);
        notificationsFragment = new NotificationsFragment();
        activeFragment = homeFragment;
    }

    public void updateNavUserInfo() {
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0));
        headerBinding.displayName.setText(auth.getCurrentUser().getDisplayName());
        headerBinding.displayEmail.setText(auth.getCurrentUser().getEmail());
        glide.load(auth.getCurrentUser().getPhotoUrl().toString()).into(headerBinding.profilePictureIv);
    }

    public void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.drawer_profile):
                case (R.id.drawer_posts):
                    startActivity(new Intent(MainPageActivity.this, ProfileActivity.class));
                    break;
                case (R.id.drawer_saved):
                    startActivity(new Intent(MainPageActivity.this, SavedActivity.class));
                    break;
                case (R.id.drawer_settings):
                    startActivity(new Intent(MainPageActivity.this, SettingsActivity.class));
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    public void setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.iconHome):
                    setCurrentFragment(homeFragment);
                    break;
                case (R.id.iconAdd):
                    bottomSheetDialog = new BottomSheetDialog(MainPageActivity.this);
                    View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                            findViewById(R.id.bottom_sheet));
                    bottomSheetDialog.setContentView(sheetView);
                    setDialogClickListeners();
                    bottomSheetDialog.show();
                    return false;
                case (R.id.iconChat):
                    setCurrentFragment(searchFragment);
                    break;
                case (R.id.iconNotification):
                    setCurrentFragment(notificationsFragment);
                    break;
                default:
                    return false;
            }
            return true;
        });
//        BadgeDrawable badge = binding.bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
//        badge.setNumber(1);
//        badge.setVisible(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.flFragment, homeFragment);
        transaction.add(R.id.flFragment, searchFragment).hide(searchFragment);
        transaction.add(R.id.flFragment, notificationsFragment).hide(notificationsFragment);
        transaction.commit();
    }

    public void setupClickListeners() {
        binding.logoutButton.setOnClickListener(v -> {
            Authentication.logout(auth, this);
            startActivity(new Intent(MainPageActivity.this, AuthActivity.class));
            finish();
        });
    }

    public void setDialogClickListeners() {
        bottomSheetDialog.findViewById(R.id.create_bottom_dialog_community_button).setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, CreateCommunityActivity.class));
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.create_bottom_dialog_post_button).setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, CreatePostActivity.class));
            bottomSheetDialog.dismiss();
        });
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();
        activeFragment = fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void buttonClicked(View v) {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void searchClicked(View v) {
        binding.bottomNavigationView.setSelectedItemId(R.id.iconChat);
        searchFragment.clickSearchView();
    }
}