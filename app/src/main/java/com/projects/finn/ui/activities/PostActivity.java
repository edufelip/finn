package com.projects.finn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
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
import com.projects.finn.ui.viewmodels.PostActivityViewModel;

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
        }
    }

    public void loadUserPhoto() {
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.userPic);
    }

    public void initializeViewModel() {
        mPostActivityViewModel = new ViewModelProvider(this).get(PostActivityViewModel.class);

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
            int count = Integer.parseInt(binding.commentsCount.getText().toString()) + 1;
            binding.commentsCount.setText(String.valueOf(count));
        });

        mPostActivityViewModel.getComments(post.getId());
    }

    public void initializeRecyclerView() {
        this.comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments, glide);
        binding.recyclerComments.setAdapter(adapter);
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setClickListeners() {
        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.createCommentButton.setOnClickListener(view -> {
            createComment();
        });
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
}