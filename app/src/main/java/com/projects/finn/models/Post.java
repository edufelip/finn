package com.projects.finn.models;

import java.util.Date;
import java.util.List;

public class Post {
    private int id;
    private String content;
    private Date date;
    private String image;
    private String community_title;
    private String community_image;
    private String user_id;
    private String user_name;
    private int community_id;
    private int likes_count;
    private int comments_count;
    private boolean isLiked;
    private List<Comment> comments;

    public Post() {

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(int community_id) {
        this.community_id = community_id;
    }

    public String getCommunity_title() {
        return community_title;
    }

    public void setCommunity_title(String community_title) {
        this.community_title = community_title;
    }

    public String getCommunity_image() {
        return community_image;
    }

    public void setCommunity_image(String community_image) {
        this.community_image = community_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
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
                ", \"user_id\":" + "\"" + user_id + "\"" +
                ", \"community_id\":" + "\"" + community_id + "\"" +
                '}';
    }
}
