package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.finn.R;
import com.projects.finn.ui.activities.homeFragments.AddFragment;
import com.projects.finn.ui.activities.homeFragments.ChatFragment;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.ui.activities.homeFragments.HomeFragment;
import com.projects.finn.ui.activities.homeFragments.NotificationsFragment;
import com.projects.finn.config.FirebaseConfig;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity implements HandleClick {
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private ChatFragment chatFragment;
    private NotificationsFragment notificationsFragment;
    private BottomSheetDialog bottomSheetDialog;
    private TextView logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initializeComponents();
        setupNavigationDrawer();
        setupBottomNavigationView();
        setupClickListeners();
    }

    public void initializeComponents() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        logoutButton = findViewById(R.id.logout_button);
        homeFragment = new HomeFragment();
        homeFragment.setInterface(MainPageActivity.this);
        addFragment = new AddFragment();
        chatFragment = new ChatFragment();
        notificationsFragment = new NotificationsFragment();
        auth = FirebaseConfig.getFirebaseAuth();
    }

    public void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
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
        setCurrentFragment(homeFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.iconHome):
                    setCurrentFragment(homeFragment);
                    break;
                case (R.id.iconAdd):
                    bottomSheetDialog = new BottomSheetDialog(MainPageActivity.this);
                    View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                            (ViewGroup) findViewById(R.id.bottom_sheet));
                    bottomSheetDialog.setContentView(sheetView);
                    setDialogClickListeners();
                    bottomSheetDialog.show();
                    break;
                case (R.id.iconChat):
                    setCurrentFragment(chatFragment);
                    break;
                case (R.id.iconNotification):
                    setCurrentFragment(notificationsFragment);
                    break;
                default:
                    return false;
            }
            return true;
        });
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
        badge.setNumber(1);
        badge.setVisible(true);
    }

    public void setupClickListeners() {
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
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

    public int setCurrentFragment(Fragment fragment) {
        return getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
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
        drawerLayout.openDrawer(GravityCompat.START);
    }
}