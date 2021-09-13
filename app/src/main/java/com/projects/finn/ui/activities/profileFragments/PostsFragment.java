package com.projects.finn.ui.activities.profileFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.projects.finn.databinding.FragmentPostsBinding;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostsFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener  {
    @Inject
    RequestManager glide;
    private FragmentPostsBinding binding;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private HandleClick handleClick;

    Post fakepost, fakepost2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        seeds();

        // set recyclerview
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this, glide);
        binding.postsRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.postsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    private void seeds() {
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
    }

    public void setInterface(HandleClick handle) {
        this.handleClick = handle;
    }

    @Override
    public void onItemClick(int position) {
        Post post = posts.get(position);
        Intent intent = new Intent(getContext(), PostActivity.class);
        intent.putExtra("postId", post.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        posts.remove(position);
        feedRecyclerAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}