package com.edufelip.finn.data.repositories;

import com.edufelip.finn.data.network.ApiService;
import com.edufelip.finn.domain.models.repositories.ICommunityRepository;
import com.edufelip.finn.domain.models.Community;
import com.edufelip.finn.domain.models.Subscription;

import java.util.List;

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
    public Flowable<List<Community>> getCommunities(String search) {
        return apiService.getCommunities(search);
    }

    @Override
    public Flowable<Community> getCommunity(int id) {
        return apiService.getCommunity(id);
    }

    @Override
    public Flowable<List<Community>> getCommunitiesFromUser(String userId) {
        return apiService.getCommunitiesFromUser(userId);
    }

    @Override
    public Flowable<Integer> getCommunitySubscribersCount(int communityId) {
        return apiService.getCommunitySubscribersCount(communityId);
    }

    @Override
    public Flowable<Community> saveCommunity(RequestBody requestBody, MultipartBody.Part image) {
        return apiService.saveCommunity(requestBody, image);
    }

    @Override
    public Flowable<Subscription> subscribeToCommunity(Subscription subscription) {
        return apiService.subscribeToCommunity(subscription);
    }

    @Override
    public Flowable<Void> unsubscribeFromCommunity(Subscription subscription) {
        return apiService.unsubscribeFromCommunity(subscription);
    }

    @Override
    public Flowable<Subscription> getSubscription(String userId, int communityId) {
        return apiService.getSubscription(userId, communityId);
    }

    @Override
    public Flowable<Void> deleteCommunity(int communityId) {
        return apiService.deleteCommunity(communityId);
    }
}
