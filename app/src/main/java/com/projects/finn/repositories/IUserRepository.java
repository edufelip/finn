package com.projects.finn.repositories;

import com.projects.finn.data.models.User;

import retrofit2.Call;

public interface IUserRepository {
    User getUser(String id);
    User getOrCreate(User user);
}
