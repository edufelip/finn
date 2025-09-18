package com.edufelip.finn.shared.data.repository

import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.CommunityCacheDataSource
import com.edufelip.finn.shared.data.mappers.toDomain
import com.edufelip.finn.shared.data.remote.source.CommunityRemoteDataSource
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultCommunityRepository(
    private val remote: CommunityRemoteDataSource,
    private val cache: CommunityCacheDataSource,
    private val ttlProvider: CacheTtlProvider,
) : CommunityRepository {

    override fun search(query: String): Flow<List<Community>> = flow {
        val cached = cache.readSearch(query, maxAgeMillis = ttlProvider.communitySearchTtlMillis)
        if (cached.isNotEmpty()) emit(cached)

        val list = remote.search(query)
        val enriched = coroutineScope {
            list.map { dto ->
                async {
                    val count = remote.getSubscribersCount(dto.id)
                    dto.toDomain(count)
                }
            }.map { it.await() }
        }
        enriched.forEach { cache.writeDetails(it) }
        cache.writeSearch(query, enriched)
        emit(enriched)
    }

    override fun getById(id: Int): Flow<Community> = flow {
        cache.getById(id, maxAgeMillis = ttlProvider.communityDetailsTtlMillis)?.let { emit(it) }

        val dto = remote.getById(id)
        val count = remote.getSubscribersCount(id)
        val domain = dto.toDomain(count)
        cache.writeDetails(domain)
        emit(domain)
    }

    override fun create(title: String, description: String?, image: ByteArray?): Flow<Community> = flow {
        val dto = remote.create(title, description, image)
        val count = remote.getSubscribersCount(dto.id)
        val domain = dto.toDomain(count)
        cache.writeDetails(domain)
        emit(domain)
    }

    override fun subscribe(userId: String, communityId: Int): Flow<Subscription> = flow {
        val subscription = remote.subscribe(userId, communityId).toDomain()
        refreshCache(communityId)
        emit(subscription)
    }

    override fun unsubscribe(userId: String, communityId: Int): Flow<Unit> = flow {
        remote.unsubscribe(userId, communityId)
        refreshCache(communityId)
        emit(Unit)
    }

    override fun getSubscription(userId: String, communityId: Int): Flow<Subscription?> = flow {
        emit(remote.getSubscription(userId, communityId)?.toDomain())
    }

    override fun delete(id: Int): Flow<Unit> = flow {
        remote.delete(id)
        cache.remove(id)
        emit(Unit)
    }

    private suspend fun refreshCache(communityId: Int) {
        val dto = remote.getById(communityId)
        val count = remote.getSubscribersCount(communityId)
        cache.writeDetails(dto.toDomain(count))
    }
}
