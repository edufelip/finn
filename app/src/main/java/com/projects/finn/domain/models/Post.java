package com.projects.finn.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@Keep
public class Post implements Parcelable {
    private int id;
    private String content;
    private Date date;
    private String image;
    @SerializedName("community_title") private String communityTitle;
    @SerializedName("community_image") private String communityImage;
    @SerializedName("user_id") private String userId;
    @SerializedName("user_name") private String userName;
    @SerializedName("community_id") private int communityId;
    @SerializedName("likes_count") private int likesCount;
    @SerializedName("comments_count") private int commentsCount;
    private boolean isLiked;
    private List<Comment> comments;

    public Post() {

    }

    protected Post(Parcel in) {
        id = in.readInt();
        content = in.readString();
        image = in.readString();
        communityTitle = in.readString();
        communityImage = in.readString();
        userId = in.readString();
        userName = in.readString();
        communityId = in.readInt();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        isLiked = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }

    public String getCommunityImage() {
        return communityImage;
    }

    public void setCommunityImage(String communityImage) {
        this.communityImage = communityImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String toJson() {
        return "{" +
                "\"id\":" + "\"" + id + "\"" +
                ", \"content\":" + "\"" + content + "\"" +
                ", \"date\":" + "\"" + date + "\"" +
                ", \"image\":" + "\"" + image + "\"" +
                ", \"user_id\":" + "\"" + userId + "\"" +
                ", \"community_id\":" + "\"" + communityId + "\"" +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(content);
        parcel.writeString(image);
        parcel.writeString(communityTitle);
        parcel.writeString(communityImage);
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeInt(communityId);
        parcel.writeInt(likesCount);
        parcel.writeInt(commentsCount);
        parcel.writeByte((byte) (isLiked ? 1 : 0));
    }
}
