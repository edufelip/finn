package com.projects.finn.ui.activities.profileFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.finn.R;
import com.projects.finn.databinding.FragmentPostsBinding;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.data.models.Post;

import java.util.ArrayList;

public class PostsFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener  {
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
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this);
        binding.postsRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.postsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    private void seeds() {
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