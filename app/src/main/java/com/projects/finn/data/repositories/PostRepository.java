package com.projects.finn.data.repositories;

import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.models.Like;
import com.projects.finn.models.Post;
import com.projects.finn.data.network.ApiService;
import com.projects.finn.models.User;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    public Flowable<List<Post>> getPostsFromUser(String userId, int page) {
        return apiService.getPostsFromUser(userId, page);
    }

    @Override
    public Flowable<Integer> getPostLikes(int postId) {
        return apiService.getPostLikes(postId);
    }

    @Override
    public Flowable<Post> savePost(RequestBody requestBody, MultipartBody.Part image) {
        return apiService.savePost(requestBody, image);
    }

    @Override
    public Flowable<Integer> findLike(int postId, String userId) {
        return apiService.findLike(postId, userId);
    }

    @Override
    public Flowable<Like> likePost(Like like) {
        return apiService.likePost(like);
    }

    @Override
    public Flowable<Void> dislikePost(int postId, User user) {
        return apiService.dislikePost(postId, user);
    }

    @Override
    public Flowable<Void> deletePost(int postId) {
        return apiService.deletePost(postId);
    }
}
