package com.projects.finn.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.projects.finn.databinding.ActivitySavedBinding;
import com.projects.finn.domain.models.Post;
import com.projects.finn.ui.adapters.FeedRecyclerAdapter;
import com.projects.finn.utils.extensions.GlideUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedActivity extends AppCompatActivity implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    GlideUtils glideUtils;
    private ActivitySavedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        initializeRecyclerView();
        setClickListeners();

        setContentView(binding.getRoot());
    }

    public void initializeRecyclerView() {
        ArrayList<Post> posts = new ArrayList<>();
        FeedRecyclerAdapter feedRecyclerAdapter = new FeedRecyclerAdapter(
            this,
            posts,
            this,
            glideUtils
        );
        binding.savedRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.savedRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onLikePost(int position) {

    }

    @Override
    public void onDislikePost(int position) {

    }
}