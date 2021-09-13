package com.projects.finn.data.repositories;

import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.models.Post;
import com.projects.finn.data.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

public class PostRepository implements IPostRepository {
    private final ApiService apiService;
    @Inject
    public PostRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Flowable<List<Post>> getUserFeed(String userId, int page) {
        return apiService.getUserFeed(userId, page);
    }

    @Override
    public Flowable<List<Post>> getPostsFromCommunity(int communityId, int page) {
        return apiService.getPostsFromCommunity(communityId, page);
    }

    @Override
    public Flowable<Integer> getPostLikes(int postId) {
        return apiService.getPostLikes(postId);
    }
}
