package com.edufelip.finn.shared.data

import com.edufelip.finn.shared.data.CacheTtlProvider.Defaults
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults

class AppleRemoteConfigCacheTtlProvider(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : CacheTtlProvider {
    override val feedCacheTtlMillis: Long
        get() = valueOrDefault("feed_cache_ttl_ms", Defaults.feedCacheTtlMillis)

    override val communitySearchTtlMillis: Long
        get() = valueOrDefault("community_search_ttl_ms", Defaults.communitySearchTtlMillis)

    override val communityDetailsTtlMillis: Long
        get() = valueOrDefault("community_details_ttl_ms", Defaults.communityDetailsTtlMillis)

    override val commentCacheTtlMillis: Long
        get() = valueOrDefault("comment_cache_ttl_ms", Defaults.commentCacheTtlMillis)

    fun remoteServer(): String =
        (defaults.stringForKey(KEY_REMOTE_SERVER) as? String)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.ensureTrailingSlash()
            ?: DEFAULT_REMOTE_SERVER

    private fun valueOrDefault(key: String, fallback: Long): Long {
        val raw = defaults.objectForKey(key) as? NSNumber ?: return fallback
        val value = raw.longLongValue
        return if (value > 0) value else fallback
    }

    private fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

    companion object {
        private const val KEY_REMOTE_SERVER = "remote_server"
        private const val DEFAULT_REMOTE_SERVER = "https://finn.dev.dashboard.eduwaldo.com/"
    }
}
