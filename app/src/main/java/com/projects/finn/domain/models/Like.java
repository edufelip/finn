package com.projects.finn.domain.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Like {
    private int id;
    @SerializedName("user_id") private String userId;
    private int postId;

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

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
