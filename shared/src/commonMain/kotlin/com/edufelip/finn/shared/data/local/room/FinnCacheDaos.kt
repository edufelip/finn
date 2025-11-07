package com.edufelip.finn.shared.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostCacheDao {
    @Query("DELETE FROM post_cache WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)

    @Query("SELECT * FROM post_cache WHERE scope = :scope ORDER BY ordering")
    suspend fun selectByScope(scope: String): List<PostCacheEntity>

    @Query("SELECT COUNT(*) FROM post_cache WHERE scope = :scope")
    suspend fun countByScope(scope: String): Int

    @Query("SELECT cache_key FROM post_cache WHERE scope = :scope ORDER BY ordering LIMIT :limit")
    suspend fun selectKeysByScope(scope: String, limit: Int): List<String>

    @Query("DELETE FROM post_cache WHERE cache_key IN (:keys)")
    suspend fun deleteByCacheKeys(keys: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostCacheEntity>)
}

@Dao
interface CommentCacheDao {
    @Query("DELETE FROM comment_cache WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)

    @Query("SELECT * FROM comment_cache WHERE scope = :scope ORDER BY updated_at_millis DESC")
    suspend fun selectByScope(scope: String): List<CommentCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentCacheEntity>)
}

@Dao
interface CommunityCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(community: CommunityCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(communities: List<CommunityCacheEntity>)

    @Query("SELECT * FROM community_cache WHERE id = :id")
    suspend fun getById(id: Long): CommunityCacheEntity?

    @Query("DELETE FROM community_cache WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CommunitySearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: CommunitySearchMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<CommunitySearchEntryEntity>)

    @Query("SELECT * FROM community_search WHERE key = :key")
    suspend fun metadataForKey(key: String): CommunitySearchMetadataEntity?

    @Query("SELECT community_id FROM community_search_entry WHERE key = :key ORDER BY position")
    suspend fun idsForKey(key: String): List<Long>

    @Query("DELETE FROM community_search_entry WHERE key = :key")
    suspend fun deleteEntries(key: String)

    @Query("DELETE FROM community_search WHERE key = :key")
    suspend fun deleteMetadata(key: String)
}
