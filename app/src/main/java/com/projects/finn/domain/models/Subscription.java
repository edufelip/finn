package com.projects.finn.domain.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Subscription {
    private int id;
    @SerializedName("user_id") private String userId;
    @SerializedName("community_id") private int communityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }
}
