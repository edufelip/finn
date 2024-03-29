package com.edufelip.finn.data.repositories;

import com.edufelip.finn.domain.models.repositories.IUserRepository;
import com.edufelip.finn.domain.models.User;
import com.edufelip.finn.data.network.ApiService;

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

    @Override
    public @NonNull Flowable<String> deleteUser(String userid) {
        return apiService.deleteUser(userid);
    }
}
