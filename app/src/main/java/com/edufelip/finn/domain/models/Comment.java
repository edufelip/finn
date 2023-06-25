package com.edufelip.finn.domain.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Keep
public class Comment {
    private int id;
    private String content;
    @SerializedName("user_id") private String userId;
    @SerializedName("post_id") private int postId;
    @SerializedName("user_image") private String userImage;
    @SerializedName("user_name") private String userName;
    private Date date;


    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
