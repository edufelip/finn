package com.projects.finn.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;
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

        getCommunityExtras();
        initializeViewModel();
        initializeRecyclerView();
        setSupportActionBar(binding.customToolbar);
        setClickListeners();
        setContentView(binding.getRoot());

        setSupportActionBar(binding.customToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void initializeViewModel() {
        mCommunityViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mCommunityViewModel.setCommunityExtra(this.community);
        mCommunityViewModel.getCommunityPosts(community.getId(), auth.getCurrentUser().getUid(), 1);
        mCommunityViewModel.getCommunity(community.getId());
        mCommunityViewModel.getSubscription(auth.getCurrentUser().getUid(), community.getId());

        mCommunityViewModel.observeCommunity().observe(this, community -> {
            if(community.getId() == -1) {
                Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if(community.getId() == -2) {
                Toast.makeText(this, getResources().getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
            String date = DATE_FORMAT.format(community.getDate());
            String capsDate = date.substring(0, 1).toUpperCase() + date.substring(1);
            this.community = community;
            binding.communityDescription.setText(community.getDescription());
            binding.subscribersCount.setText(String.valueOf(community.getSubscribersCount()));
            binding.communityDate.setText(capsDate);
            checkAdmin();
        });

        mCommunityViewModel.observePosts().observe(this, posts -> {
            this.posts = new ArrayList<>(posts);
            feedRecyclerAdapter.setPosts(this.posts);
            checkEmptyRecycler(posts.size());
        });

        mCommunityViewModel.observeUpdatedPost().observe(this, post -> {
            feedRecyclerAdapter.updatePost(post);
        });

        mCommunityViewModel.observeUpdateSubscription().observe(this, subscription -> {
            switch (subscription.getId()) {
                case -1: {
                    Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
                    break;
                }
                case -2: {
                    binding.followBtn.setText(getResources().getString(R.string.subscribe));
                    int value = Integer.parseInt(binding.subscribersCount.getText().toString()) - 1;
                    community.setSubscribersCount(value);
                    binding.subscribersCount.setText(String.valueOf(value));
                    break;
                }
                default: {
                    binding.followBtn.setText(getResources().getString(R.string.unsubscribe));
                    int value = Integer.parseInt(binding.subscribersCount.getText().toString()) + 1;
                    community.setSubscribersCount(value);
                    binding.subscribersCount.setText(String.valueOf(value));
                    break;
                }
            }
        });

        mCommunityViewModel.observeSubscription().observe(this, subscription -> {
            switch (subscription.getId()) {
                case -1: {
                    Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
                    break;
                }
                case -2: {
                    binding.followBtn.setText(getResources().getString(R.string.subscribe));
                    break;
                }
                default: {
                    binding.followBtn.setText(getResources().getString(R.string.unsubscribe));
                }
            }
        });

        mSharedLikeViewModel.observeLike().observe(this, like -> {
            if(like.getId() == -1) {
                Toast.makeText(this, getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCommunityExtras() {
        community = (Community) getIntent().getParcelableExtra("community");
        if(community != null) {
            binding.communityTitle.setText(community.getTitle());
            glide.load(BuildConfig.BACKEND_IP + "/" + community.getImage()).into(binding.communityImage);
        }
    }

    public void checkEmptyRecycler(int size) {
        if(size == 0) {
            binding.recyclerCommunityPosts.setVisibility(View.GONE);
            binding.emptyRecyclerLayout.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerCommunityPosts.setVisibility(View.VISIBLE);
            binding.emptyRecyclerLayout.setVisibility(View.GONE);
        }
    }

    public void checkAdmin() {
        if(auth.getCurrentUser() != null) {
            if(community.getUser_id().equals(auth.getCurrentUser().getUid())) {
                binding.textViewOptions.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initializeRecyclerView() {
        this.posts = new ArrayList<>();
        feedRecyclerAdapter = new FeedRecyclerAdapter(this, posts, this, glide);
        binding.recyclerCommunityPosts.setAdapter(feedRecyclerAdapter);
        binding.recyclerCommunityPosts.setLayoutManager(new LinearLayoutManager(this));
        // implement pagination
    }

    @SuppressLint("RestrictedApi")
    public void setClickListeners() {
        binding.backButton.setOnClickListener(view -> {
           finish();
        });

        binding.followBtn.setOnClickListener(view -> {
            if(auth.getCurrentUser() != null) {
                String text = binding.followBtn.getText().toString();
                if(text.equals(getResources().getString(R.string.subscribe))) {
                    mCommunityViewModel.subscribeToCommunity(auth.getCurrentUser().getUid(), community.getId());
                } else if(text.equals(getResources().getString(R.string.unsubscribe))) {
                    mCommunityViewModel.unsubscribeFromCommunity(auth.getCurrentUser().getUid(), community.getId());
                }
            }
        });

        binding.textViewOptions.setOnClickListener(v -> {
            MenuBuilder menuBuilder =new MenuBuilder(this);
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate(R.menu.community_menu, menuBuilder);
            MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, binding.textViewOptions);
            optionsMenu.setForceShowIcon(true);

            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                    if (item.getItemId() == R.id.communityDelete) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CommunityActivity.this);
                        dialog.setTitle(getResources().getString(R.string.delete_community));
                        dialog.setMessage(getResources().getString(R.string.are_you_sure_delete_community));
                        dialog.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            mCommunityViewModel.deleteCommunity(auth.getCurrentUser().getUid(), community);
                        });
                        dialog.setNegativeButton(getResources().getString(R.string.no), null);
                        dialog.show();
                        return true;
                    }
                    return false;
                }

                @Override
                public void onMenuModeChange(@NonNull MenuBuilder menu) {

                }
            });
            optionsMenu.show();
        });
    }

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Post post = data.getParcelableExtra("post");
                            feedRecyclerAdapter.updatePostActivityResult(post);
                        }
                    }
                }
            }
    );

    @Override
    public void onItemClick(int position) {
        Post post = posts.get(position);
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", post);
        postActivityResultLauncher.launch(intent);
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
        mSharedLikeViewModel.likePost(this.posts.get(position).getId(), id);
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

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("community", community);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}