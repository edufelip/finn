package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.projects.finn.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        setClickListeners();

        setContentView(binding.getRoot());
    }

    public void setClickListeners() {
        binding.settingsBackButton.setOnClickListener(v -> finish());
    }
}