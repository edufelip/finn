package com.projects.finn.ui.activities.homeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.qrcode.decoder.Version;
import com.projects.finn.data.models.User;
import com.projects.finn.databinding.FragmentHomeBinding;
import com.projects.finn.ui.activities.AuthActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.data.models.Post;
import com.projects.finn.ui.viewmodel.HomeFragmentViewModel;
import com.projects.finn.utils.Authentication;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.Flowable;

import java.util.ArrayList;

import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    FirebaseAuth auth;
    private FragmentHomeBinding binding;
    private HandleClick handleClick;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private User user;
    private ArrayList<Post> posts;
    private HomeFragmentViewModel mHomeFragmentViewModel;
    Post fakepost, fakepost2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initializeViewModel();
        checkLoggedUser();
        initializeComponents();
        setClickListeners();
        seeds();
        initializeRecyclerView();

        return binding.getRoot();
    }

    public void checkLoggedUser() {
        User tempUser = new User("1", auth.getCurrentUser().getDisplayName());
        mHomeFragmentViewModel.getUser(tempUser);
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
    }

    public void forceLogout() {
        Authentication.logout(auth, requireActivity());
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.putExtra("Error", "An error has ocurred, please log again later");
        startActivity(intent);
        requireActivity().finish();
    }

    public void initializeRecyclerView() {
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this);
        binding.feedRecyclerView.setAdapter(feedRecyclerAdapter);
        binding.feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void seeds() {
        fakepost = new Post();
        fakepost.setId("1");
        fakepost.setUserName("Fake Username One");
        fakepost.setCommunityName("FakeSubreddit");
        fakepost.setTitle("Fake post title");
        fakepost.setLikes(212);
        fakepost.setComments(14);
        fakepost.setDescription("Fake description, random text, random words");

        fakepost2 = new Post();
        fakepost2.setId("12");
        fakepost2.setUserName("Faker Username Two");
        fakepost2.setCommunityName("SecondFakeSubreddit");
        fakepost2.setTitle("Another fake post title");
        fakepost2.setLikes(12);
        fakepost2.setComments(7);
        fakepost2.setDescription("Another fake post description with another fake words");

        posts.add(fakepost);
        posts.add(fakepost2);
    }

    public void initializeComponents() {
        posts = new ArrayList<>();
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

