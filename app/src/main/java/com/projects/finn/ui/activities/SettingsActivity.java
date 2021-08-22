package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.projects.finn.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeComponents();
        setClickListeners();
    }

    public void initializeComponents() {
        backButton = findViewById(R.id.settings_back_button);
    }

    public void setClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }
}