package com.projects.finn.data.repositories;

import com.projects.finn.data.network.ApiService;
import com.projects.finn.data.repositories.interfaces.ICommentRepository;
import com.projects.finn.models.Comment;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

public class CommentRepository implements ICommentRepository {
    private final ApiService apiService;

    @Inject
    public CommentRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Flowable<List<Comment>> getCommentsPost(int postId) {
        return apiService.getCommentsPost(postId);
    }

    @Override
    public Flowable<Comment> saveComment(Comment comment) {
        return apiService.saveComment(comment);
    }
}
