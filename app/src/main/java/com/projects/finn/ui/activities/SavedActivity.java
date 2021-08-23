package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import com.projects.finn.databinding.ActivitySavedBinding;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.data.models.Post;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity implements FeedRecyclerAdapter.RecyclerClickListener {
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
        fakepost.setId("1");
        fakepost.setUserName("Fake Username One");
        fakepost.setCommunityName("FakeSubreddit");
        fakepost.setTitle("Fake post title");
        fakepost.setLikes(212);
        fakepost.setComments(14);
        fakepost.setDescription("Fake description, random text, random words");

        fakepost2 = new Post();
        fakepost2.setId("12");
        fakepost2.setUserName("Faker Username Two");
        fakepost2.setCommunityName("SecondFakeSubreddit");
        fakepost2.setTitle("Another fake post title");
        fakepost2.setLikes(12);
        fakepost2.setComments(7);
        fakepost2.setDescription("Another fake post description with another fake words");

        posts.add(fakepost);
        posts.add(fakepost2);

        feedRecyclerAdapter = new FeedRecyclerAdapter(this, posts, this);
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
}