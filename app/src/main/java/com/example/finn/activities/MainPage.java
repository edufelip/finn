package com.example.finn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.finn.R;
import com.example.finn.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity {
    private Button button;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        button = findViewById(R.id.button2);
        auth = FirebaseConfig.getFirebaseAuth();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(MainPage.this, AuthActivity.class));
                finish();
            }
        });
    }
}