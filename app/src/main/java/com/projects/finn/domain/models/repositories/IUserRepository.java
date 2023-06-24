package com.projects.finn.domain.models.repositories;

import com.projects.finn.domain.models.User;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;

public interface IUserRepository {
    @NonNull Flowable<User> getUser(String id);
    @NonNull Flowable<User> createUser(User user);
    @NonNull Flowable<String> deleteUser(String userid);
}
