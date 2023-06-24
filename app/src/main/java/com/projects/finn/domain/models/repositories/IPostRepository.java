package com.projects.finn.domain.models.repositories;

import com.projects.finn.domain.models.Like;
import com.projects.finn.domain.models.Post;
import com.projects.finn.domain.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface IPostRepository {
    Flowable<List<Post>> getUserFeed(String userId, int page);

    Flowable<Integer> getPostLikes(int postId);

    Flowable<List<Post>> getPostsFromCommunity(int communityId, int page);

    Flowable<List<Post>> getPostsFromUser(String userId, int page);

    Flowable<Post> savePost(RequestBody requestBody, MultipartBody.Part image);

    Flowable<Integer> findLike(int postId, String userId);

    Flowable<Like> likePost(Like like);

    Flowable<Void> dislikePost(int postId, User user);

    Flowable<Void> deletePost(int postId);
}
