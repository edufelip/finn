package com.edufelip.finn.ui.activities.profileFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.edufelip.finn.R;
import com.edufelip.finn.databinding.FragmentPostsBinding;
import com.edufelip.finn.domain.models.Post;
import com.edufelip.finn.domain.models.User;
import com.edufelip.finn.ui.activities.PostActivity;
import com.edufelip.finn.ui.adapters.FeedRecyclerAdapter;
import com.edufelip.finn.ui.viewmodels.PostsFragmentViewModel;
import com.edufelip.finn.ui.viewmodels.SharedLikeViewModel;
import com.edufelip.finn.utils.extensions.GlideUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostsFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    GlideUtils glideUtils;
    @Inject
    FirebaseAuth auth;
    private SharedLikeViewModel mSharedLikeViewModel;
    private FragmentPostsBinding binding;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);

        initializeRecyclerView();
        initializeViewModel();

        return binding.getRoot();
    }

    public void initializeRecyclerView() {
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this, glideUtils);
        binding.postsRecyclerview.setAdapter(feedRecyclerAdapter);
        binding.postsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void initializeViewModel() {
        String id = auth.getCurrentUser().getUid();
        String name = auth.getCurrentUser().getDisplayName();

        PostsFragmentViewModel mPostsFragmentViewModel = new ViewModelProvider(this).get(PostsFragmentViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mPostsFragmentViewModel.setUserName(name);

        mPostsFragmentViewModel.observePosts().observe(getViewLifecycleOwner(), posts -> {
            this.posts = new ArrayList<>(posts);
            feedRecyclerAdapter.setPosts(this.posts);
            checkEmptyRecycler(posts.size());
        });

        mPostsFragmentViewModel.observeUpdatedPost().observe(getViewLifecycleOwner(), post -> {
            feedRecyclerAdapter.updatePost(post);
        });

        mPostsFragmentViewModel.getUserPosts(id, 1);

        mSharedLikeViewModel.observeLike().observe(getViewLifecycleOwner(), like -> {
            if (like.getId() == -1) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
            }
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

    public void checkEmptyRecycler(int size) {
        if (size == 0) {
            binding.postsRecyclerview.setVisibility(View.GONE);
            binding.emptyRecyclerState.setVisibility(View.VISIBLE);
        } else {
            binding.postsRecyclerview.setVisibility(View.VISIBLE);
            binding.emptyRecyclerState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(int position) {
        Post post = posts.get(position);
        Intent intent = new Intent(getContext(), PostActivity.class);
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
        post.setLikesCount(post.getLikesCount() + 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
        String id = auth.getCurrentUser().getUid();
        mSharedLikeViewModel.likePost(this.posts.get(position).getId(), id);
    }

    @Override
    public void onDislikePost(int position) {
        Post post = posts.get(position);
        post.setLiked(true);
        post.setLikesCount(post.getLikesCount() - 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
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