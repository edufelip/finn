package com.edufelip.finn.domain.models

import androidx.annotation.Keep
import java.util.Date

@Keep
data class User(
    var id: String? = null,
    var name: String? = null,
    var photo: String? = null,
    var date: Date? = null,
)
