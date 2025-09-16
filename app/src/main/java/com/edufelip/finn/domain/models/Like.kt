package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Like(
    var id: Int = 0,
    @SerializedName("user_id") var userId: String? = null,
    var postId: Int = 0,
)
