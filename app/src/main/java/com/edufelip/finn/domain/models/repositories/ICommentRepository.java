package com.edufelip.finn.domain.models.repositories;

import com.edufelip.finn.domain.models.Comment;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public interface ICommentRepository {
    Flowable<List<Comment>> getCommentsPost(int id);

    Flowable<Comment> saveComment(Comment comment);
}
