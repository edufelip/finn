package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.projects.finn.R;
import com.projects.finn.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseConfig.getFirebaseAuth();
        checkUserLogged();
    }

    public void checkUserLogged() {
        if(auth.getCurrentUser() != null) {
            openMainPage();
            return;
        }
        openAuthActivity();
    }

    public void openMainPage() {
        startActivity(new Intent(this, MainPageActivity.class));
        finish();
    }

    public void openAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}