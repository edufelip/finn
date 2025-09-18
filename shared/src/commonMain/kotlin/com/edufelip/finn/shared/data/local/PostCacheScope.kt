package com.edufelip.finn.shared.data.local

sealed class PostCacheScope(val key: String) {
    data class Feed(val userId: String) : PostCacheScope("feed:$userId")
    data class Community(val communityId: Int) : PostCacheScope("community:$communityId")
    data class User(val userId: String) : PostCacheScope("user:$userId")
}
