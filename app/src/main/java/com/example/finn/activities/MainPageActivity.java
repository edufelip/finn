package com.example.finn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finn.R;
import com.example.finn.activities.homeFragments.AddFragment;
import com.example.finn.activities.homeFragments.ChatFragment;
import com.example.finn.activities.homeFragments.HandleClick;
import com.example.finn.activities.homeFragments.HomeFragment;
import com.example.finn.activities.homeFragments.NotificationsFragment;
import com.example.finn.config.FirebaseConfig;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.drawer_profile):
                        startActivity(new Intent(MainPageActivity.this, ProfileActivity.class));
                        break;
                    case (R.id.drawer_saved):
                        // open saved posts activity
                        break;
                    case (R.id.drawer_posts):
                        // open your posts activity
                        break;
                    case (R.id.drawer_settings):
                        // open settings activity
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public void setupBottomNavigationView() {
        setCurrentFragment(homeFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.iconHome):
                        setCurrentFragment(homeFragment);
                        break;
                    case (R.id.iconAdd):
                        setCurrentFragment(addFragment);
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
            }
        });
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
        badge.setNumber(1);
        badge.setVisible(true);
    }

    public void setupClickListeners() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(MainPageActivity.this, AuthActivity.class));
                finish();
            }
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