package com.edufelip.finn.shared.navigation

object DeepLinks {
    const val BASE_HOST: String = "finn.app"

    fun postUrl(id: Int, scheme: String = "https", host: String = BASE_HOST): String =
        "$scheme://$host/post/$id"

    fun communityUrl(id: Int, scheme: String = "https", host: String = BASE_HOST): String =
        "$scheme://$host/community/$id"
}
