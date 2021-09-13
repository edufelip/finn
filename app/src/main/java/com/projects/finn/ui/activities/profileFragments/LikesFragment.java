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

import com.bumptech.glide.RequestManager;
import com.projects.finn.R;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LikesFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener  {
    @Inject
    RequestManager glide;
    private RecyclerView feed;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts;
    private HandleClick handleClick;

    Post fakepost, fakepost2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_likes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();

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

        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this, glide);
        feed.setAdapter(feedRecyclerAdapter);
        feed.setLayoutManager(new LinearLayoutManager(getContext()));

//        LikesFragmentViewModel mViewModel = new ViewModelProvider(this).get(LikesFragmentViewModel.class);
//        mViewModel.getUserListObserver().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
//            @Override
//            public void onChanged(List<Post> posts) {
//                if(posts != null) {
//                    posts = posts;
//                    feedRecyclerAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//        mViewModel.makeApiCall();
    }

    public void initializeComponents() {
        feed = getView().findViewById(R.id.likes_recyclerview);
        posts = new ArrayList<Post>();
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
}