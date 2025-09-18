package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto

interface CommunityRemoteDataSource {
    suspend fun search(query: String): List<CommunityDto>
    suspend fun getById(id: Int): CommunityDto
    suspend fun getSubscribersCount(id: Int): Int
    suspend fun create(
        title: String,
        description: String?,
        image: ByteArray?,
    ): CommunityDto
    suspend fun subscribe(userId: String, communityId: Int): SubscriptionDto
    suspend fun unsubscribe(userId: String, communityId: Int)
    suspend fun getSubscription(userId: String, communityId: Int): SubscriptionDto?
    suspend fun delete(id: Int)
}
