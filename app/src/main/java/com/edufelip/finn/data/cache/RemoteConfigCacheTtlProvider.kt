package com.edufelip.finn.data.cache

import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.utils.RemoteConfigUtils
import javax.inject.Inject

class RemoteConfigCacheTtlProvider @Inject constructor(
    private val remoteConfig: RemoteConfigUtils,
) : CacheTtlProvider {
    override val feedCacheTtlMillis: Long
        get() = remoteConfig.getFeedCacheTtlMillis()

    override val communitySearchTtlMillis: Long
        get() = remoteConfig.getCommunitySearchTtlMillis()

    override val communityDetailsTtlMillis: Long
        get() = remoteConfig.getCommunityDetailsTtlMillis()

    override val commentCacheTtlMillis: Long
        get() = remoteConfig.getCommentCacheTtlMillis()
}
