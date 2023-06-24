package com.projects.finn.ui.activities.homeFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.adapters.CommunitySearchAdapter;
import com.projects.finn.databinding.FragmentSearchBinding;
import com.projects.finn.models.Community;
import com.projects.finn.ui.activities.CommunityActivity;
import com.projects.finn.ui.viewmodels.SearchFragmentViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment implements CommunitySearchAdapter.RecyclerClickListener {
    @Inject
    RequestManager glide;
    @Inject
    FirebaseAuth auth;
    FragmentSearchBinding binding;
    private HandleClick handleClick;
    private SearchFragmentViewModel mSearchFragmentViewModel;
    private ArrayList<Community> communities;
    private CommunitySearchAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        loadUserPhoto();
        initializeViewModel();
        initializeRecyclerView();
        setupListeners();

        return binding.getRoot();
    }

    public void initializeViewModel() {
        mSearchFragmentViewModel = new ViewModelProvider(this).get(SearchFragmentViewModel.class);

        mSearchFragmentViewModel.observeCommunities().observe(getViewLifecycleOwner(), communities -> {
            this.communities = new ArrayList<>(communities);
            adapter.setCommunities(this.communities);
            binding.swipeLayout.setRefreshing(false);
        });

        mSearchFragmentViewModel.observeUpdatedCommunity().observe(getViewLifecycleOwner(), community -> {
            adapter.updateCommunity(community);
        });

        mSearchFragmentViewModel.getTrendingCommunities("");
    }

    public void initializeRecyclerView() {
        this.communities = new ArrayList<>();
        adapter = new CommunitySearchAdapter(getContext(), communities, this, glide);
        binding.recyclerTrending.setAdapter(adapter);
        binding.recyclerTrending.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void loadUserPhoto() {
        glide.load(auth.getCurrentUser().getPhotoUrl()).into(binding.profilePictureIcon);
    }

    public void clickSearchView() {
        binding.searchView.setIconified(false);
        binding.searchView.performClick();
    }

    public void setupListeners() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String query = s;
                mSearchFragmentViewModel.getTrendingCommunities(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchFragmentViewModel.getTrendingCommunities("");
            }
        });

        binding.profilePictureIcon.setOnClickListener(view -> handleClick.buttonClicked(view));
    }

    ActivityResultLauncher<Intent> communityActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Community community = data.getParcelableExtra("community");
                            adapter.updateCommunityActivityResult(community);
                        }
                    }
                }
            }
    );

    public void setInterface(HandleClick handle){
        this.handleClick = handle;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), CommunityActivity.class);
        Community community = new Community();
        community.setId(communities.get(position).getId());
        community.setTitle(communities.get(position).getTitle());
        community.setImage(communities.get(position).getImage());
        intent.putExtra("community", community);
        communityActivityResultLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
