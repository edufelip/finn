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

    fun getRemoteServerAddress(): String = remote.getString(KEY_REMOTE_SERVER)

    fun getIsFacebookAuthEnabled(): Boolean = remote.getBoolean(IS_FACEBOOK_AUTH_ENABLED)

    fun getCommentsPageSize(): Int = remote.getLong(KEY_COMMENTS_PAGE_SIZE).toInt()

    companion object {
        private const val KEY_REMOTE_SERVER = "remote_server"
        private const val IS_FACEBOOK_AUTH_ENABLED = "is_facebook_auth_enabled"
        private const val KEY_COMMENTS_PAGE_SIZE = "comments_page_size"
    }
}
