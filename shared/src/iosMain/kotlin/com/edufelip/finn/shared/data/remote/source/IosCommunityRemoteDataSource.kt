package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto

class IosCommunityRemoteDataSource(
    private val api: IosBackendApi,
) : CommunityRemoteDataSource {
    override suspend fun search(query: String): List<CommunityDto> =
        api.getCommunities(query)

    override suspend fun getById(id: Int): CommunityDto =
        api.getCommunity(id)

    override suspend fun getSubscribersCount(id: Int): Int =
        api.getCommunitySubscribers(id)

    override suspend fun create(title: String, description: String?, image: ByteArray?): CommunityDto =
        api.createCommunity(
            CommunityCreatePayload(
                title = title,
                description = description,
                image = image,
            ),
        )

    override suspend fun subscribe(userId: String, communityId: Int): SubscriptionDto =
        api.subscribe(SubscriptionDto(id = 0, userId = userId, communityId = communityId))

    override suspend fun unsubscribe(userId: String, communityId: Int) {
        api.unsubscribe(SubscriptionDto(id = 0, userId = userId, communityId = communityId))
    }

    override suspend fun getSubscription(userId: String, communityId: Int): SubscriptionDto? =
        api.getSubscription(userId, communityId)

    override suspend fun delete(id: Int) {
        api.deleteCommunity(id)
    }
}
