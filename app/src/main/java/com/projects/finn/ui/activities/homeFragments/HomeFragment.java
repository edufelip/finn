package com.projects.finn.ui.activities.homeFragments;

import static com.projects.finn.utils.Constants.QUERY_PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.models.User;
import com.projects.finn.databinding.FragmentHomeBinding;
import com.projects.finn.ui.activities.AuthActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.models.Post;
import com.projects.finn.ui.viewmodels.HomeFragmentViewModel;
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
            requestPosts();
        }
        initializeRecyclerView();
        setClickListeners();
        return binding.getRoot();
    }

    public void loadUserPhoto() {
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.profilePictureIcon);
    }

    public void checkLoggedUser() {
        User tempUser = new User(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName());
        mHomeFragmentViewModel.getUser(tempUser);
    }

    public void requestPosts() {
        mHomeFragmentViewModel.getPosts(auth.getCurrentUser().getUid(), nextPage);
    }

    public void initializeViewModel() {
        mHomeFragmentViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
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
        });
        mHomeFragmentViewModel.observeUpdatedPost().observe(getViewLifecycleOwner(), updatedPost -> {
            feedRecyclerAdapter.updatePost(updatedPost);
            isLoading = false;
        });
        mHomeFragmentViewModel.observeNextPage().observe(getViewLifecycleOwner(), number -> {
            this.nextPage = number;
        });
    }

    public void forceLogout() {
        Authentication.logout(auth, requireActivity());
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.putExtra("Error", "An error has ocurred, please log again later");
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
                    mHomeFragmentViewModel.getPosts(user.getId(), nextPage);
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

