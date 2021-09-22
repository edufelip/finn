package com.projects.finn.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;
import com.projects.finn.adapters.CommentsAdapter;
import com.projects.finn.databinding.ActivityPostBinding;
import com.projects.finn.models.Comment;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;
import com.projects.finn.models.User;
import com.projects.finn.ui.viewmodels.PostActivityViewModel;
import com.projects.finn.ui.viewmodels.SharedLikeViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostActivity extends AppCompatActivity {
    @Inject
    RequestManager glide;
    @Inject
    FirebaseAuth auth;
    private ActivityPostBinding binding;
    private PostActivityViewModel mPostActivityViewModel;
    private ArrayList<Comment> comments;
    private CommentsAdapter adapter;
    private SharedLikeViewModel mSharedLikeViewModel;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());

        getPostExtras();
        loadUserPhoto();
        initializeViewModel();
        initializeRecyclerView();
        setClickListeners();

        setContentView(binding.getRoot());
    }

    public void getPostExtras() {
        post = (Post) getIntent().getParcelableExtra("post");
        if(post != null) {
            binding.postCommunity.setText(post.getCommunity_title());
            String text = "Posted by: " + post.getUser_name();
            binding.postSource.setText(text);
            binding.postContent.setText(post.getContent());
            binding.likesCount.setText(String.valueOf(post.getLikes_count()));
            binding.commentsCount.setText(String.valueOf(post.getComments_count()));
            if(post.getCommunity_image() != null) {
                glide.load(BuildConfig.BACKEND_IP + "/" + post.getCommunity_image()).into(binding.communityPictureIcon);
            }
            if(post.getImage() != null) {
                glide.load(BuildConfig.BACKEND_IP + "/" + post.getImage()).into(binding.postImage);
            }
            if(post.isLiked()) {
                binding.likeButton.setChecked(true);
            }
        }
        checkAdmin();
    }

    public void loadUserPhoto() {
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.userPic);
    }

    public void initializeViewModel() {
        mPostActivityViewModel = new ViewModelProvider(this).get(PostActivityViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mPostActivityViewModel.observeComments().observe(this, comments -> {
            this.comments = new ArrayList<>(comments);
            adapter.setComments(this.comments);
        });

        mPostActivityViewModel.observeUpdatedComment().observe(this, comment -> {
            adapter.updateComment(comment);
        });

        mPostActivityViewModel.observeCreatedComment().observe(this, createdComment -> {
            if(createdComment.getId() == -1) {
                Toast.makeText(this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            binding.commentEdittext.setText("");
            mPostActivityViewModel.getComments(post.getId());
            post.setComments_count(post.getComments_count() + 1);
            binding.commentsCount.setText(String.valueOf(post.getComments_count()));
        });

        mPostActivityViewModel.observePost().observe(this, post -> {
            if(post.getId() == -1) {
                Toast.makeText(this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if(post.getId() == -2) {
                Toast.makeText(this, "Post successfully deleted", Toast.LENGTH_SHORT).show();
                this.post.setUser_id("-2");
                finish();
                return;
            }
        });

        mSharedLikeViewModel.observeLike().observe(this, like -> {
            switch (like.getId()) {
                case (-1) : {
                    Toast.makeText(this, "Something wrong happened, try again later", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                case (-2) : {
                    int count = post.getLikes_count() - 1;
                    post.setLikes_count(count);
                    post.setLiked(false);
                    binding.likesCount.setText(String.valueOf(count));
                    break;
                }
                default : {
                    int count = post.getLikes_count() + 1;
                    post.setLikes_count(count);
                    post.setLiked(true);
                    binding.likesCount.setText(String.valueOf(count));
                }
            }
        });

        mPostActivityViewModel.getComments(post.getId());
    }

    public void initializeRecyclerView() {
        this.comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments, glide);
        binding.recyclerComments.setAdapter(adapter);
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
    }

    public void checkAdmin() {
        if(auth.getCurrentUser() != null) {
            if(post.getUser_id().equals(auth.getCurrentUser().getUid())) {
                binding.textViewOptions.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void setClickListeners() {
        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.createCommentButton.setOnClickListener(view -> {
            createComment();
        });

        binding.likeButton.setOnClickListener(view -> {
            if(binding.likeButton.isChecked()) {
                likePost();
            } else {
                dislikePost();
            }
        });

        binding.shareButton.setOnClickListener(view -> {
            Toast.makeText(this, "Not available yet", Toast.LENGTH_SHORT).show();
        });

        binding.textViewOptions.setOnClickListener(view -> {
            MenuBuilder menuBuilder =new MenuBuilder(this);
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate(R.menu.community_menu, menuBuilder);
            MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, binding.textViewOptions);
            optionsMenu.setForceShowIcon(true);

            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                    if (item.getItemId() == R.id.communityDelete) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PostActivity.this);
                        dialog.setTitle("Delete post?");
                        dialog.setMessage("Are you sure? Once delete your post will be lost forever");
                        dialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                            mPostActivityViewModel.deletePost(auth.getCurrentUser().getUid(), post);
                        });
                        dialog.setNegativeButton("No", null);
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

    public void likePost() {
        String userId = auth.getCurrentUser().getUid();
        mSharedLikeViewModel.likePost(post.getId(), userId);
    }

    public void dislikePost() {
        String userId = auth.getCurrentUser().getUid();
        String name = auth.getCurrentUser().getDisplayName();
        User user = new User();
        user.setId(userId);
        user.setName(name);
        mSharedLikeViewModel.dislikePost(post.getId(), user);
    }

    public void createComment() {
        String content = binding.commentEdittext.getText().toString();
        if(content.isEmpty()) {
            Toast.makeText(this, "Please fill the post field", Toast.LENGTH_SHORT).show();
            return;
        }
        Comment comment = new Comment();
        comment.setUser_id(auth.getCurrentUser().getUid());
        comment.setPost_id(post.getId());
        comment.setContent(content);
        mPostActivityViewModel.createComment(comment);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("post", post);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}