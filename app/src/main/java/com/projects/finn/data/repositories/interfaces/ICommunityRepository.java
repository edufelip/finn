package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.Community;
import com.projects.finn.models.Subscription;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface ICommunityRepository {
    Flowable<List<Community>> getCommunities(String search);

    Flowable<Community> getCommunity(int id);

    Flowable<Community> saveCommunity(RequestBody requestBody, MultipartBody.Part image);

    Flowable<Integer> getCommunitySubscribersCount(int communityId);

    Flowable<List<Community>> getCommunitiesFromUser(String userId);

    Flowable<Subscription> subscribeToCommunity(Subscription subscription);

    Flowable<Void> unsubscribeFromCommunity(Subscription subscription);

    Flowable<Subscription> getSubscription(String userId, int communityId);

    Flowable<Void> deleteCommunity(int communityId);
}
