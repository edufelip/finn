package com.edufelip.finn.shared.data.local

import androidx.room.withTransaction
import com.edufelip.finn.shared.data.local.room.CommunityCacheDao
import com.edufelip.finn.shared.data.local.room.CommunityCacheEntity
import com.edufelip.finn.shared.data.local.room.CommunitySearchDao
import com.edufelip.finn.shared.data.local.room.CommunitySearchEntryEntity
import com.edufelip.finn.shared.data.local.room.CommunitySearchMetadataEntity
import com.edufelip.finn.shared.data.local.room.FinnCacheDatabase
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.util.currentTimeMillis

interface CommunityCacheDataSource {
    suspend fun writeSearch(query: String, communities: List<Community>)
    suspend fun readSearch(query: String, maxAgeMillis: Long? = null): List<Community>
    suspend fun writeDetails(community: Community)
    suspend fun getById(id: Int, maxAgeMillis: Long? = null): Community?
    suspend fun clearSearch(query: String)
    suspend fun remove(id: Int)
}

class RoomCommunityCacheDataSource(
    private val database: FinnCacheDatabase,
    private val timeProvider: () -> Long = { currentTimeMillis() },
) : CommunityCacheDataSource {

    private val cacheDao: CommunityCacheDao
        get() = database.communityCacheDao()
    private val searchDao: CommunitySearchDao
        get() = database.communitySearchDao()

    override suspend fun writeSearch(query: String, communities: List<Community>) {
        val key = searchKey(query)
        val now = timeProvider()
        database.withTransaction {
            searchDao.deleteEntries(key)
            if (communities.isEmpty()) {
                searchDao.deleteMetadata(key)
                return@withTransaction
            }

            searchDao.insertMetadata(
                CommunitySearchMetadataEntity(key = key, updatedAtMillis = now),
            )
            cacheDao.insertAll(
                communities.map { it.toEntity(now) },
            )
            searchDao.insertEntries(
                communities.mapIndexed { index, community ->
                    CommunitySearchEntryEntity(
                        key = key,
                        communityId = community.id.toLong(),
                        position = index.toLong(),
                    )
                },
            )
        }
    }

    override suspend fun readSearch(query: String, maxAgeMillis: Long?): List<Community> {
        val key = searchKey(query)
        val metadata = searchDao.metadataForKey(key) ?: return emptyList()
        if (maxAgeMillis != null && timeProvider() - metadata.updatedAtMillis > maxAgeMillis) {
            clearSearch(query)
            return emptyList()
        }
        val ids = searchDao.idsForKey(key)
        if (ids.isEmpty()) return emptyList()
        return ids.mapNotNull { id ->
            cacheDao.getById(id)?.takeIf { row ->
                maxAgeMillis == null || timeProvider() - row.updatedAtMillis <= maxAgeMillis
            }?.toDomain()
        }
    }

    override suspend fun writeDetails(community: Community) {
        val now = timeProvider()
        cacheDao.insert(community.toEntity(now))
    }

    override suspend fun getById(id: Int, maxAgeMillis: Long?): Community? {
        val row = cacheDao.getById(id.toLong()) ?: return null
        if (maxAgeMillis != null && timeProvider() - row.updatedAtMillis > maxAgeMillis) {
            remove(id)
            return null
        }
        return row.toDomain()
    }

    override suspend fun clearSearch(query: String) {
        val key = searchKey(query)
        database.withTransaction {
            searchDao.deleteEntries(key)
            searchDao.deleteMetadata(key)
        }
    }

    override suspend fun remove(id: Int) {
        cacheDao.deleteById(id.toLong())
    }

    private fun searchKey(query: String) = query.trim().lowercase()
}

private fun CommunityCacheEntity.toDomain(): Community =
    Community(
        id = id.toInt(),
        title = title,
        description = description,
        image = image,
        subscribersCount = subscribersCount.toInt(),
        ownerId = ownerId,
        createdAtMillis = createdAtMillis,
        cachedAtMillis = updatedAtMillis,
    )

private fun Community.toEntity(now: Long) =
    CommunityCacheEntity(
        id = id.toLong(),
        title = title,
        description = description,
        image = image,
        subscribersCount = subscribersCount.toLong(),
        ownerId = ownerId,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = now,
    )
