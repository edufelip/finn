package com.projects.finn.data.repositories;

import com.projects.finn.data.network.ApiService;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.models.Community;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    public Flowable<Integer> getCommunitySubscribersCount(int communityId) {
        return apiService.getCommunitySubscribersCount(communityId);
    }

    @Override
    public Flowable<Community> saveCommunity(RequestBody requestBody, MultipartBody.Part image) {
        return apiService.saveCommunity(requestBody, image);
    }
}
