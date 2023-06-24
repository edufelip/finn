package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.bumptech.glide.RequestManager;
import com.projects.finn.databinding.ActivitySavedBinding;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedActivity extends AppCompatActivity implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    RequestManager glide;
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
        FeedRecyclerAdapter feedRecyclerAdapter = new FeedRecyclerAdapter(this, posts, this);
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