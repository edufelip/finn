package com.projects.finn.data.repositories.interfaces;

import com.projects.finn.models.Community;

import io.reactivex.rxjava3.core.Flowable;

public interface ICommunityRepository {
    Flowable<Community> getCommunity(int id);

    Flowable<Community> saveCommunity(Community community);
}
