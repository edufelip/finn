package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.User;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;

public interface IUserRepository {
    @NonNull Flowable<User> getUser(String id);
    @NonNull Flowable<User> createUser(User user);
}
