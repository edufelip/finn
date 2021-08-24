package com.projects.finn.ui.activities.homeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projects.finn.R;
import com.projects.finn.databinding.FragmentHomeBinding;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.adapters.FeedRecyclerAdapter;
import com.projects.finn.data.models.Post;
import com.projects.finn.utils.Check;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements FeedRecyclerAdapter.RecyclerClickListener {
    private FragmentHomeBinding binding;
    private HandleClick handleClick;
    private ImageView imageView;
    private RecyclerView feed;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private ArrayList<Post> posts;

    Post fakepost, fakepost2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initializeComponents();
        setClickListeners();
        seeds();
        Check.isInternetOn(getContext());

        // setup recyclerview
        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), posts, this);
        feed.setAdapter(feedRecyclerAdapter);
        feed.setLayoutManager(new LinearLayoutManager(getContext()));

//        HomeFragmentViewModel mViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
//        mViewModel.getUserListObserver().observe(getViewLifecycleOwner(), posts -> {
//            if(posts != null) {
//                posts = posts;
//                feedRecyclerAdapter.notifyDataSetChanged();
//            }
//        });
//        mViewModel.makeApiCall();
        return binding.getRoot();
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
        imageView = getView().findViewById(R.id.profilePictureIcon);
        feed = getView().findViewById(R.id.feed_recycler_view);
        posts = new ArrayList<Post>();
    }

    public void setClickListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick.buttonClicked(v);
            }
        });
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

