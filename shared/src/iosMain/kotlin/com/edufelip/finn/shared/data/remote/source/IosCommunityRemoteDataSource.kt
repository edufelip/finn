package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto

class IosCommunityRemoteDataSource : CommunityRemoteDataSource {
    override suspend fun search(query: String): List<CommunityDto> = sampleCommunities()

    override suspend fun getById(id: Int): CommunityDto =
        sampleCommunities().firstOrNull { it.id == id } ?: sampleCommunities().first()

    override suspend fun getSubscribersCount(id: Int): Int = 1_000

    override suspend fun create(title: String, description: String?, image: ByteArray?): CommunityDto =
        CommunityDto(id = (1..100_000).random(), title = title, description = description)

    override suspend fun subscribe(userId: String, communityId: Int): SubscriptionDto =
        SubscriptionDto(id = 1, userId = userId, communityId = communityId)

    override suspend fun unsubscribe(userId: String, communityId: Int) {}

    override suspend fun getSubscription(userId: String, communityId: Int): SubscriptionDto? =
        SubscriptionDto(id = 1, userId = userId, communityId = communityId)

    override suspend fun delete(id: Int) {}

    private fun sampleCommunities(): List<CommunityDto> = listOf(
        CommunityDto(id = 1, title = "Kotlin", description = "Kotlin discussions"),
        CommunityDto(id = 2, title = "Android", description = "Android dev"),
    )
}
