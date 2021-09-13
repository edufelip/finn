package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.Community;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface ICommunityRepository {
    Flowable<Community> getCommunity(int id);

    Flowable<Community> saveCommunity(RequestBody requestBody, MultipartBody.Part image);

    Flowable<Integer> getCommunitySubscribersCount(int communityId);
}
