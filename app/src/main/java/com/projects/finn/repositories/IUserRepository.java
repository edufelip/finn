package com.projects.finn.repositories;

import androidx.lifecycle.LiveData;

import com.projects.finn.data.models.User;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

public interface IUserRepository {
    @NonNull Observable<User> getUser(String id);
    @NonNull Observable<User> createUser(User user);
}
