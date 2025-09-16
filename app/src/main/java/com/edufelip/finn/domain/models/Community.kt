package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class Community(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var image: String = "",
    @SerializedName("user_id") var userId: String? = null,
    var date: Date? = null,
    var subscribersCount: Int = 0,
)
