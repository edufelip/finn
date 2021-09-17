package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.RequestManager;
import com.projects.finn.databinding.ActivitySavedBinding;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
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
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts;
    private HandleClick handleClick;

    Post fakepost, fakepost2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        initializeComponents();
        setClickListeners();

        fakepost = new Post();
        fakepost.setId(1);
        fakepost.setUser_name("Fake Username One");
        fakepost.setCommunity_title("FakeSubreddit");
        fakepost.setContent("Fake post title");
        fakepost.setLikes_count(212);
        fakepost.setComments_count(14);

        fakepost2 = new Post();
        fakepost2.setId(12);
        fakepost2.setUser_name("Faker Username Two");
        fakepost2.setCommunity_title("SecondFakeSubreddit");
        fakepost2.setContent("Another fake post title");
        fakepost2.setLikes_count(12);
        fakepost2.setComments_count(7);

        posts.add(fakepost);
        posts.add(fakepost2);

        feedRecyclerAdapter = new FeedRecyclerAdapter(this, posts, this, glide);
        binding.savedRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.savedRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        setContentView(binding.getRoot());
    }

    public void initializeComponents() {
        posts = new ArrayList<Post>();
    }

    public void setClickListeners() {
        binding.savedBackButton.setOnClickListener(v -> finish());
    }

    public void setInterface(HandleClick handle) {
        this.handleClick = handle;
    }

    @Override
    public void onItemClick(int position) {
        Post post = posts.get(position);
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("postId", post.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        posts.remove(position);
        feedRecyclerAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onLikePost(int position) {

    }

    @Override
    public void onDislikePost(int position) {

    }
}