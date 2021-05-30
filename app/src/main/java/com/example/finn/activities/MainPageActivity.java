package com.example.finn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finn.R;
import com.example.finn.activities.homeFragments.AddFragment;
import com.example.finn.activities.homeFragments.ChatFragment;
import com.example.finn.activities.homeFragments.HandleClick;
import com.example.finn.activities.homeFragments.HomeFragment;
import com.example.finn.activities.homeFragments.NotificationsFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainPageActivity extends AppCompatActivity implements HandleClick {
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private ChatFragment chatFragment;
    private NotificationsFragment notificationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initializeComponents();
        setupNavigationView();
        setupBottomNavigationView();
        setupClickListeners();
    }

    public void initializeComponents() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        homeFragment = new HomeFragment();
        homeFragment.setInterface(MainPageActivity.this);
        addFragment = new AddFragment();
        chatFragment = new ChatFragment();
        notificationsFragment = new NotificationsFragment();
    }

    public void setupNavigationView() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.itemOne):
                        Toast.makeText(MainPageActivity.this, "CLICKED ITEM 1", Toast.LENGTH_SHORT).show();
                    case (R.id.itemTwo):
                        Toast.makeText(MainPageActivity.this, "CLICKED ITEM 2", Toast.LENGTH_SHORT).show();
                    case (R.id.itemThree):
                        Toast.makeText(MainPageActivity.this, "CLICKED ITEM 3", Toast.LENGTH_SHORT).show();
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
                String itemName = item.getTitle().toString();
                switch (itemName) {
                    case "Home":
                        setCurrentFragment(homeFragment);
                        break;
                    case "Add":
                        setCurrentFragment(addFragment);
                        break;
                    case "Chat":
                        setCurrentFragment(chatFragment);
                        break;
                    case "Notifications":
                        setCurrentFragment(notificationsFragment);
                        break;
                }
                return true;
            }
        });
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
        badge.setNumber(1);
        badge.setVisible(true);
    }

    public void setupClickListeners() {
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