package com.projects.finn.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.util.Date;

@Keep
public class Community implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String image;
    private String user_id;
    private Date date;
    private int subscribersCount;

    public Community() {

    }

    protected Community(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        user_id = in.readString();
        subscribersCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(user_id);
        dest.writeInt(subscribersCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Community> CREATOR = new Creator<Community>() {
        @Override
        public Community createFromParcel(Parcel in) {
            return new Community(in);
        }

        @Override
        public Community[] newArray(int size) {
            return new Community[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(int subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public String toJson() {
        return "{" +
                "\"id\":" + "\"" + id + "\"" +
                ", \"title\":" + "\"" + title + "\"" +
                ", \"description\":" + "\"" + description + "\"" +
                ", \"image\":" + "\"" + image + "\"" +
                ", \"user_id\":" + "\"" + user_id + "\"" +
                ", \"date\":" + "\"" + date + "\"" +
                '}';
    }
}
