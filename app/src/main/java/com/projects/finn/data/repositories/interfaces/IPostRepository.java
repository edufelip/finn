package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.Post;

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
}
