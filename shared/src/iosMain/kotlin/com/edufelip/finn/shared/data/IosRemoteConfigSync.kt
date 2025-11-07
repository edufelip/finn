package com.edufelip.finn.shared.data

import com.edufelip.finn.shared.network.createIosHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults

private const val REMOTE_CONFIG_ENDPOINT = "https://finn.dev.dashboard.eduwaldo.com/mobile-config/ios.json"
private const val KEY_REMOTE_SERVER = "remote_server"

class IosRemoteConfigSync(
    private val httpClient: HttpClient = createIosHttpClient(),
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) {
    fun refreshAsync() {
        CoroutineScope(Dispatchers.Default).launch {
            runCatching { refresh() }
                .onFailure { println("IosRemoteConfigSync failed: ${it.message}") }
        }
    }

    suspend fun refresh() = withContext(Dispatchers.Default) {
        val payload: RemoteConfigPayload = httpClient.get(REMOTE_CONFIG_ENDPOINT).body()
        payload.feedCacheTtlMillis?.storeLong("feed_cache_ttl_ms")
        payload.communitySearchTtlMillis?.storeLong("community_search_ttl_ms")
        payload.communityDetailsTtlMillis?.storeLong("community_details_ttl_ms")
        payload.commentCacheTtlMillis?.storeLong("comment_cache_ttl_ms")
        payload.remoteServer?.storeString(KEY_REMOTE_SERVER)
        payload.commentsPageSize?.storeLong("comments_page_size")
    }

    private fun Long.storeLong(key: String) {
        defaults.setObject(NSNumber(longLong = this), forKey = key)
    }

    private fun String.storeString(key: String) {
        defaults.setObject(this, forKey = key)
    }
}

@Serializable
private data class RemoteConfigPayload(
    @SerialName("remote_server")
    val remoteServer: String? = null,
    @SerialName("feed_cache_ttl_ms")
    val feedCacheTtlMillis: Long? = null,
    @SerialName("community_search_ttl_ms")
    val communitySearchTtlMillis: Long? = null,
    @SerialName("community_details_ttl_ms")
    val communityDetailsTtlMillis: Long? = null,
    @SerialName("comment_cache_ttl_ms")
    val commentCacheTtlMillis: Long? = null,
    @SerialName("comments_page_size")
    val commentsPageSize: Long? = null,
)
