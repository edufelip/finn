package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class Comment(
    var id: Int = 0,
    var content: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("post_id") var postId: Int = 0,
    @SerializedName("user_image") var userImage: String? = null,
    @SerializedName("user_name") var userName: String? = null,
    var date: Date? = null,
)
