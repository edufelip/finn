package com.projects.finn.ui.activities.profileFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.databinding.FragmentPostsBinding;
import com.projects.finn.models.User;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.ui.activities.homeFragments.HandleClick;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;
import com.projects.finn.ui.viewmodels.PostsFragmentViewModel;
import com.projects.finn.ui.viewmodels.SharedLikeViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostsFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener  {
    @Inject
    RequestManager glide;
    @Inject
    FirebaseAuth auth;
    private PostsFragmentViewModel mPostsFragmentViewModel;
    private SharedLikeViewModel mSharedLikeViewModel;
    private FragmentPostsBinding binding;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private HandleClick handleClick;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);

        initializeRecyclerView();
        initializeViewModel();

        return binding.getRoot();
    }

    public void initializeRecyclerView() {
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this, glide);
        binding.postsRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.postsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void initializeViewModel() {
        String id = auth.getCurrentUser().getUid();
        String name = auth.getCurrentUser().getDisplayName();

        mPostsFragmentViewModel = new ViewModelProvider(this).get(PostsFragmentViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mPostsFragmentViewModel.setUserName(name);

        mPostsFragmentViewModel.observePosts().observe(getViewLifecycleOwner(), posts -> {
            this.posts = new ArrayList<>(posts);
            feedRecyclerAdapter.setPosts(this.posts);
        });

        mPostsFragmentViewModel.observeUpdatedPost().observe(getViewLifecycleOwner(), post -> {
            feedRecyclerAdapter.updatePost(post);
        });

        mPostsFragmentViewModel.getUserPosts(id, 1);

        mSharedLikeViewModel.observeLike().observe(getViewLifecycleOwner(), like -> {
            if(like.getId() == -1) {
                Toast.makeText(getContext(), "Something wrong happened, try liking again later", Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onLikePost(int position) {
        String id = auth.getCurrentUser().getUid();
        mSharedLikeViewModel.likePost(id, this.posts.get(position).getId());
    }

    @Override
    public void onDislikePost(int position) {
        User user = new User();
        user.setId(auth.getCurrentUser().getUid());
        mSharedLikeViewModel.dislikePost(this.posts.get(position).getId(), user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}