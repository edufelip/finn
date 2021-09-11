package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.projects.finn.R;
import com.projects.finn.databinding.ActivityCommunityBinding;
import com.projects.finn.models.Community;

public class CommunityActivity extends AppCompatActivity {
    private ActivityCommunityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());

        Community community = (Community) getIntent().getParcelableExtra("community");
        if(community != null) {
            binding.communityTitle.setText(community.getTitle());
            binding.communityDescription.setText(community.getDescription());

        }
        setSupportActionBar(binding.customToolbar);
        setContentView(binding.getRoot());
    }
}