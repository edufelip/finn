package com.example.finn.activities.homeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finn.R;
import com.example.finn.activities.PostActivity;
import com.example.finn.adapters.FeedRecyclerAdapter;
import com.example.finn.data.Post;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener {
    private HandleClick handleClick;
    private ImageView imageView;
    private RecyclerView feed;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts;

    Post fakepost, fakepost2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        setClickListeners();

        fakepost = new Post();
        fakepost.setId("1");
        fakepost.setUserName("Eduardo Felipe");
        fakepost.setCommunityName("SubredditDoEdu");
        fakepost.setTitle("30 razões para não escolher programação");
        fakepost.setLikes(212);
        fakepost.setComments(14);
        fakepost.setDescription("Sei lá, é isso ai, tururu");

        fakepost2 = new Post();
        fakepost2.setId("12");
        fakepost2.setUserName("Rodrigo Legal");
        fakepost2.setCommunityName("SubredditDoRodrigo");
        fakepost2.setTitle("1 razão para escolher programação");
        fakepost2.setLikes(12);
        fakepost2.setComments(7);
        fakepost2.setDescription("Sei lá, é isso ai, HIWIWWIWIWIIW");

        posts.add(fakepost);
        posts.add(fakepost2);

        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this);
        feed.setAdapter(feedRecyclerAdapter);
        feed.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void initializeComponents() {
        imageView = getView().findViewById(R.id.profilePictureIcon);
        feed = getView().findViewById(R.id.feed_recycler_view);
        posts = new ArrayList<Post>();
    }

    public void setClickListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick.buttonClicked(v);
            }
        });
    }

    public void setInterface(HandleClick handle){
        this.handleClick = handle;
    }

    @Override
    public void onItemClick(int position) {
        Post post = posts.get(position);
        Intent intent = new Intent(getContext(), PostActivity.class);
        intent.putExtra("postId", post.getId());
        startActivity(intent);
    }

    public void onDeleteClick(int position) {
        posts.remove(position);
        feedRecyclerAdapter.notifyItemRemoved(position);
    }
}

