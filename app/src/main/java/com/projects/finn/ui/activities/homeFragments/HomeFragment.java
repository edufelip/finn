package com.projects.finn.ui.activities.homeFragments;

import static com.projects.finn.utils.Constants.QUERY_PAGE_SIZE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.R;
import com.projects.finn.models.User;
import com.projects.finn.databinding.FragmentHomeBinding;
import com.projects.finn.ui.activities.AuthActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;
import com.projects.finn.ui.viewmodels.HomeFragmentViewModel;
import com.projects.finn.ui.viewmodels.SharedLikeViewModel;
import com.projects.finn.utils.Authentication;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;

import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    FirebaseAuth auth;
    @Inject
    RequestManager glide;
    private FragmentHomeBinding binding;
    private HandleClick handleClick;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private User user;
    private ArrayList<Post> posts;
    private HomeFragmentViewModel mHomeFragmentViewModel;
    private SharedLikeViewModel mSharedLikeViewModel;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isScrolling = false;
    private int nextPage = 1;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("check_user", 1);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initializeViewModel();
        loadUserPhoto();
        if(savedInstanceState == null) {
            checkLoggedUser();
            mHomeFragmentViewModel.getPosts(auth.getCurrentUser().getUid(), nextPage, false);
        }
        initializeRecyclerView();
        setClickListeners();
        setSwipeRefresh();
        return binding.getRoot();
    }

    public void loadUserPhoto() {
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.profilePictureIcon);
    }

    public void checkLoggedUser() {
        String id = auth.getCurrentUser().getUid();
        String photo = auth.getCurrentUser().getPhotoUrl().toString();
        String displayName = auth.getCurrentUser().getDisplayName();
        User tempUser = new User();
        tempUser.setId(id);
        tempUser.setName(displayName);
        tempUser.setPhoto(photo);
        mHomeFragmentViewModel.getUser(tempUser);
    }

    public void initializeViewModel() {
        mHomeFragmentViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        mSharedLikeViewModel = new ViewModelProvider(this).get(SharedLikeViewModel.class);

        mHomeFragmentViewModel.observeUser().observe(getViewLifecycleOwner(), user -> {
                if(user.getId().equals("-1")) {
                    forceLogout();
                    return;
                }
                this.user = user;
            }
        );
        mHomeFragmentViewModel.observePosts().observe(getViewLifecycleOwner(), posts -> {
            this.posts = new ArrayList<>(posts);
            feedRecyclerAdapter.setPosts(this.posts);
            binding.swipeLayout.setRefreshing(false);
            binding.emptyRecyclerLayout.setRefreshing(false);
            checkEmptyRecycler(posts.size());
        });
        mHomeFragmentViewModel.observeUpdatedPost().observe(getViewLifecycleOwner(), updatedPost -> {
            feedRecyclerAdapter.updatePost(updatedPost);
            isLoading = false;
        });
        mHomeFragmentViewModel.observeNextPage().observe(getViewLifecycleOwner(), number -> {
            this.nextPage = number;
        });
        mSharedLikeViewModel.observeLike().observe(getViewLifecycleOwner(), like -> {
            if(like.getId() == -1) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_try_again_later), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkEmptyRecycler(int size) {
        if(size == 0) {
            binding.swipeLayout.setVisibility(View.GONE);
            binding.emptyRecyclerLayout.setVisibility(View.VISIBLE);
        } else {
            binding.swipeLayout.setVisibility(View.VISIBLE);
            binding.emptyRecyclerLayout.setVisibility(View.GONE);
        }
    }

    public void forceLogout() {
        Authentication.logout(auth, requireActivity());
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.putExtra("Error", getResources().getString(R.string.error_occurred_log_later));
        startActivity(intent);
        requireActivity().finish();
    }

    public void initializeRecyclerView() {
        this.posts = new ArrayList<>();
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this, glide);
        binding.feedRecyclerView.setAdapter(feedRecyclerAdapter);
        binding.feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.feedRecyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                boolean isNotLoadingNotLastPage = !isLoading && !isLastPage;
                boolean isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
                boolean isNotAtBeginning = firstVisibleItemPosition >= 0;
                boolean isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE;
                boolean isListFullOrOdd = posts.size() % 10 == 0 || posts.size() >= 40;
                boolean shouldPaginate = isNotLoadingNotLastPage && isAtLastItem && isNotAtBeginning
                        && isTotalMoreThanVisible && isScrolling && isListFullOrOdd;
                if(shouldPaginate) {
                    mHomeFragmentViewModel.getPosts(user.getId(), nextPage, false);
                    isScrolling = false;
                    isLoading = true;
                } else {
                    binding.feedRecyclerView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    public void setClickListeners() {
        binding.profilePictureIcon.setOnClickListener(v -> handleClick.buttonClicked(v));

        binding.fakeSearchView.setOnClickListener(v -> {
            handleClick.searchClicked(v);
        });
    }

    public void setSwipeRefresh() {
        binding.swipeLayout.setOnRefreshListener(() -> {
            mHomeFragmentViewModel.getPosts(user.getId(), 1, true);
        });

        binding.emptyRecyclerLayout.setOnRefreshListener(() -> {
            mHomeFragmentViewModel.getPosts(user.getId(), 1, true);
        });
    }

    public void setInterface(HandleClick handle){
        this.handleClick = handle;
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
        post.setLikes_count(post.getLikes_count() + 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
        mSharedLikeViewModel.likePost(this.posts.get(position).getId(), this.user.getId());
    }

    @Override
    public void onDislikePost(int position) {
        Post post = posts.get(position);
        post.setLiked(false);
        post.setLikes_count(post.getLikes_count() - 1);
        feedRecyclerAdapter.updatePost(post);
        feedRecyclerAdapter.notifyItemChanged(position);
        mSharedLikeViewModel.dislikePost(this.posts.get(position).getId(), this.user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

