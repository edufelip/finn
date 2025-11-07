package com.edufelip.finn.utils

import com.edufelip.finn.BuildConfig
import com.edufelip.finn.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfigUtils {
    private val remote: FirebaseRemoteConfig = fetchAndActivate()

    private fun fetchAndActivate(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().build(),
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        val cacheExpiration = if (BuildConfig.DEBUG) 1L else 3600L
        remoteConfig.fetch(cacheExpiration).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig.activate()
            }
        }
        return remoteConfig
    }

    fun getRemoteServerAddress(): String {
        val configured = remote.getString(KEY_REMOTE_SERVER).trim()
        val resolved = if (configured.isNotEmpty()) configured else BuildConfig.DEFAULT_REMOTE_SERVER
        val normalized = normalizeBaseUrl(resolved)
        require(normalized.startsWith("https://")) {
            "Remote server must use HTTPS. Provided value: $resolved"
        }
        return normalized
    }

    fun getIsFacebookAuthEnabled(): Boolean = remote.getBoolean(IS_FACEBOOK_AUTH_ENABLED)

    fun getCommentsPageSize(): Int = remote.getLong(KEY_COMMENTS_PAGE_SIZE).toInt()

    fun getFeedCacheTtlMillis(): Long = remote.getLong(KEY_FEED_CACHE_TTL_MS).takeIf { it > 0 } ?: DEFAULT_FEED_TTL

    fun getCommunitySearchTtlMillis(): Long = remote.getLong(KEY_COMMUNITY_SEARCH_TTL_MS).takeIf { it > 0 } ?: DEFAULT_COMMUNITY_SEARCH_TTL

    fun getCommunityDetailsTtlMillis(): Long = remote.getLong(KEY_COMMUNITY_DETAILS_TTL_MS).takeIf { it > 0 } ?: DEFAULT_COMMUNITY_DETAILS_TTL

    fun getCommentCacheTtlMillis(): Long = remote.getLong(KEY_COMMENT_CACHE_TTL_MS).takeIf { it > 0 } ?: DEFAULT_COMMENT_TTL

    companion object {
        private const val KEY_REMOTE_SERVER = "remote_server"
        private const val IS_FACEBOOK_AUTH_ENABLED = "is_facebook_auth_enabled"
        private const val KEY_COMMENTS_PAGE_SIZE = "comments_page_size"
        private const val KEY_FEED_CACHE_TTL_MS = "feed_cache_ttl_ms"
        private const val KEY_COMMUNITY_SEARCH_TTL_MS = "community_search_ttl_ms"
        private const val KEY_COMMUNITY_DETAILS_TTL_MS = "community_details_ttl_ms"
        private const val KEY_COMMENT_CACHE_TTL_MS = "comment_cache_ttl_ms"

        private const val DEFAULT_FEED_TTL = 5 * 60 * 1000L
        private const val DEFAULT_COMMUNITY_SEARCH_TTL = 15 * 60 * 1000L
        private const val DEFAULT_COMMUNITY_DETAILS_TTL = 10 * 60 * 1000L
        private const val DEFAULT_COMMENT_TTL = 5 * 60 * 1000L

        private fun normalizeBaseUrl(value: String): String =
            if (value.endsWith("/")) value else "$value/"
    }
}
