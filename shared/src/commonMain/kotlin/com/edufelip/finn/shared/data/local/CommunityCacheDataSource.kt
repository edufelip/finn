package com.edufelip.finn.shared.data.local

import com.edufelip.finn.shared.cache.Community_cache
import com.edufelip.finn.shared.cache.Community_search
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.domain.model.Community

interface CommunityCacheDataSource {
    suspend fun writeSearch(query: String, communities: List<Community>)
    suspend fun readSearch(query: String, maxAgeMillis: Long? = null): List<Community>
    suspend fun writeDetails(community: Community)
    suspend fun getById(id: Int, maxAgeMillis: Long? = null): Community?
    suspend fun clearSearch(query: String)
    suspend fun remove(id: Int)
}

class SqlDelightCommunityCacheDataSource(
    private val database: FinnDatabase,
    private val timeProvider: () -> Long = { System.currentTimeMillis() },
) : CommunityCacheDataSource {

    private val queries get() = database.cacheQueries

    override suspend fun writeSearch(query: String, communities: List<Community>) {
        val key = searchKey(query)
        val now = timeProvider()
        database.transaction {
            queries.deleteSearchEntries(key)
            if (communities.isEmpty()) {
                queries.deleteSearchMetadata(key)
                return@transaction
            }

            queries.insertSearchMetadata(key, now)
            communities.forEachIndexed { index, community ->
                queries.insertCommunity(
                    id = community.id.toLong(),
                    title = community.title,
                    description = community.description,
                    image = community.image,
                    subscribers_count = community.subscribersCount.toLong(),
                    owner_id = community.ownerId,
                    created_at_millis = community.createdAtMillis,
                    updated_at_millis = now,
                )
                queries.insertSearchEntry(
                    key = key,
                    community_id = community.id.toLong(),
                    position = index.toLong(),
                )
            }
        }
    }

    override suspend fun readSearch(query: String, maxAgeMillis: Long?): List<Community> {
        val key = searchKey(query)
        val metadata: Community_search = queries.selectSearchMetadata(key).executeAsOneOrNull() ?: return emptyList()
        if (maxAgeMillis != null && timeProvider() - metadata.updated_at_millis > maxAgeMillis) {
            clearSearch(query)
            return emptyList()
        }
        val ids = queries.selectCommunitiesForSearch(key).executeAsList()
        if (ids.isEmpty()) return emptyList()
        return ids.mapNotNull { id ->
            queries.selectCommunityById(id).executeAsOneOrNull()?.takeIf { row ->
                maxAgeMillis == null || timeProvider() - row.updated_at_millis <= maxAgeMillis
            }?.toDomain()
        }
    }

    override suspend fun writeDetails(community: Community) {
        val now = timeProvider()
        queries.insertCommunity(
            id = community.id.toLong(),
            title = community.title,
            description = community.description,
            image = community.image,
            subscribers_count = community.subscribersCount.toLong(),
            owner_id = community.ownerId,
            created_at_millis = community.createdAtMillis,
            updated_at_millis = now,
        )
    }

    override suspend fun getById(id: Int, maxAgeMillis: Long?): Community? {
        val row = queries.selectCommunityById(id.toLong()).executeAsOneOrNull() ?: return null
        if (maxAgeMillis != null && timeProvider() - row.updated_at_millis > maxAgeMillis) {
            remove(id)
            return null
        }
        return row.toDomain()
    }

    override suspend fun clearSearch(query: String) {
        val key = searchKey(query)
        database.transaction {
            queries.deleteSearchEntries(key)
            queries.deleteSearchMetadata(key)
        }
    }

    override suspend fun remove(id: Int) {
        queries.deleteCommunityById(id.toLong())
    }

    private fun searchKey(query: String) = query.trim().lowercase()
}

private fun Community_cache.toDomain(): Community =
    Community(
        id = id.toInt(),
        title = title,
        description = description,
        image = image,
        subscribersCount = subscribers_count.toInt(),
        ownerId = owner_id,
        createdAtMillis = created_at_millis,
        cachedAtMillis = updated_at_millis,
    )
