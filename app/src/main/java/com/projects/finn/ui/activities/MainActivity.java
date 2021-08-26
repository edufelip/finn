package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.projects.finn.R;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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