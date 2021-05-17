package com.example.finn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.finn.R;
import com.example.finn.config.FirebaseConfig;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        startActivity(new Intent(this, MainPage.class));
        finish();
    }

    public void openAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}