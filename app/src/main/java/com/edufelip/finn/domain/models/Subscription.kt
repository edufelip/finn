package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Subscription(
    var id: Int = 0,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("community_id") var communityId: Int = 0,
)
