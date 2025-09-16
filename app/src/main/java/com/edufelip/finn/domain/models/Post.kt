package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class Post(
    var id: Int = 0,
    var content: String? = null,
    var date: Date? = null,
    var image: String? = null,
    @SerializedName("community_title") var communityTitle: String? = null,
    @SerializedName("community_image") var communityImage: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("user_name") var userName: String? = null,
    @SerializedName("community_id") var communityId: Int = 0,
    @SerializedName("likes_count") var likesCount: Int = 0,
    @SerializedName("comments_count") var commentsCount: Int = 0,
    var isLiked: Boolean = false,
    var comments: List<Comment>? = null,
)
