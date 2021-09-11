package com.projects.finn.data.repositories;

import com.projects.finn.data.repositories.interfaces.IUserRepository;
import com.projects.finn.models.User;
import com.projects.finn.data.network.ApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;

public class UserRepository implements IUserRepository {
    private final ApiService apiService;

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public @NonNull Flowable<User> getUser(String userId) {
        return apiService.getUser(userId);
    }

    @Override
    public @NonNull Flowable<User> createUser (User user){
        return apiService.createUser(user);
    }
}
