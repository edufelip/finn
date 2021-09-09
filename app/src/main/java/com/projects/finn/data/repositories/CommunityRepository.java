package com.projects.finn.data.repositories;

import com.projects.finn.data.network.ApiService;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.models.Community;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

public class CommunityRepository implements ICommunityRepository {
    private final ApiService apiService;

    @Inject
    public CommunityRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Flowable<Community> getCommunity(int id) {
        return apiService.getCommunity(id);
    }

    @Override
    public Flowable<Community> saveCommunity(Community community) {
        return apiService.saveCommunity(community);
    }
}
