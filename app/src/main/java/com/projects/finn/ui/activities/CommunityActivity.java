package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.BuildConfig;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.databinding.ActivityCommunityBinding;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;
import com.projects.finn.models.User;
import com.projects.finn.ui.viewmodels.CommunityViewModel;
import com.projects.finn.ui.viewmodels.SharedLikeViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommunityActivity extends AppCompatActivity implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    RequestManager glide;
    @Inject
    FirebaseAuth auth;
    private ActivityCommunityBinding binding;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private CommunityViewModel mCommunityViewModel;
    private SharedLikeViewModel mSharedLikeViewModel;
    private ArrayList<Post> posts;
    private Community community;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());

        setCommunityExtras();
        initializeViewModel();
        initializeRecyclerView();
        setSupportActionBar(binding.customToolbar);
        setClickListeners();
        setContentView(binding.getRoot());
    }

    public void initializeViewModel() {
        mCommunityViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mCommunityViewModel.setCommunityExtra(this.community);
        mCommunityViewModel.getCommunityPosts(community.getId(), auth.getCurrentUser().getUid(), 1);
        mCommunityViewModel.getCommunity(community.getId());

        mCommunityViewModel.observeCommunity().observe(this, community -> {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
            String date = DATE_FORMAT.format(community.getDate());
            String capsDate = date.substring(0, 1).toUpperCase() + date.substring(1);
            this.community = community;
            binding.communityDescription.setText(community.getDescription());
            binding.communitySubscribers.setText(String.valueOf(community.getSubscribersCount()));
            binding.communityDate.setText(capsDate);
        });

        mCommunityViewModel.observePosts().observe(this, posts -> {
            this.posts = new ArrayList<>(posts);
            feedRecyclerAdapter.setPosts(this.posts);
        });

        mCommunityViewModel.observeUpdatedPost().observe(this, post -> {
            feedRecyclerAdapter.updatePost(post);
        });

        mSharedLikeViewModel.observeLike().observe(this, like -> {
            if(like.getId() == -1) {
                Toast.makeText(this, "Something wrong happened, try liking again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCommunityExtras() {
        community = (Community) getIntent().getParcelableExtra("community");
        if(community != null) {
            binding.communityTitle.setText(community.getTitle());
            glide.load(BuildConfig.BACKEND_IP + "/" + community.getImage()).into(binding.communityImage);
        }
    }

    public void initializeRecyclerView() {
        this.posts = new ArrayList<>();
        feedRecyclerAdapter = new FeedRecyclerAdapter(this, posts, this, glide);
        binding.recyclerCommunityPosts.setAdapter(feedRecyclerAdapter);
        binding.recyclerCommunityPosts.setLayoutManager(new LinearLayoutManager(this));
        // implement pagination
    }

    public void setClickListeners() {
        binding.backButton.setOnClickListener(view -> {
           finish();
        });
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
        Post post = posts.get(position);
        post.setLiked(true);
        post.setLikes_count(post.getLikes_count() + 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
        String id = auth.getCurrentUser().getUid();
        mSharedLikeViewModel.likePost(id, this.posts.get(position).getId());
    }

    @Override
    public void onDislikePost(int position) {
        Post post = posts.get(position);
        post.setLiked(true);
        post.setLikes_count(post.getLikes_count() - 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
        User user = new User();
        user.setId(auth.getCurrentUser().getUid());
        mSharedLikeViewModel.dislikePost(this.posts.get(position).getId(), user);
    }
}