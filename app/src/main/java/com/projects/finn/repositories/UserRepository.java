package com.projects.finn.repositories;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.projects.finn.data.models.User;
import com.projects.finn.data.network.ApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRepository implements IUserRepository {
    private final ApiService apiService;

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public @NonNull Observable<User> getUser(String userId) {
        return apiService.getUser(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public @NonNull Observable<User> createUser(User user) {
        return apiService.createUser(user)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
