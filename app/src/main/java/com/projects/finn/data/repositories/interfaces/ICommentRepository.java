package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.Comment;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public interface ICommentRepository {
    Flowable<List<Comment>> getCommentsPost(int id);
}
