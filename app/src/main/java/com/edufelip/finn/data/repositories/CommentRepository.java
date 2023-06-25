package com.edufelip.finn.data.repositories;

import com.edufelip.finn.data.network.ApiService;
import com.edufelip.finn.domain.models.repositories.ICommentRepository;
import com.edufelip.finn.domain.models.Comment;

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
