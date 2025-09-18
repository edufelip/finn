package com.edufelip.finn.shared.data

interface CacheTtlProvider {
    val feedCacheTtlMillis: Long
    val communitySearchTtlMillis: Long
    val communityDetailsTtlMillis: Long
    val commentCacheTtlMillis: Long

    object Defaults : CacheTtlProvider {
        override val feedCacheTtlMillis: Long = 5 * 60 * 1000L
        override val communitySearchTtlMillis: Long = 15 * 60 * 1000L
        override val communityDetailsTtlMillis: Long = 10 * 60 * 1000L
        override val commentCacheTtlMillis: Long = 5 * 60 * 1000L
    }
}
