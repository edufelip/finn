package com.example.finn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.finn.R;
import com.example.finn.config.FirebaseConfig;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity {
    private Button button;
    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment addFragment = new AddFragment();
        Fragment chatFragment = new ChatFragment();
        Fragment notificationsFragment = new NotificationsFragment();

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.iconHome: setCurrentFragment(homeFragment);
                    case R.id.iconAdd: setCurrentFragment(addFragment);
                    case R.id.iconChat: setCurrentFragment(chatFragment);
                    case R.id.iconNotification: setCurrentFragment(notificationsFragment);
                }
                return true;
            }
        });

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.iconNotification);
        badge.setNumber(1);
        badge.setVisible(true);
    }

    public int setCurrentFragment(Fragment fragment) {
        return getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}