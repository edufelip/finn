package com.edufelip.finn.shared.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_cache")
data class PostCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val cacheKey: String,
    val scope: String,
    @ColumnInfo(name = "post_id")
    val postId: Long,
    val content: String,
    @ColumnInfo(name = "community_id")
    val communityId: Long?,
    @ColumnInfo(name = "community_title")
    val communityTitle: String?,
    @ColumnInfo(name = "community_image")
    val communityImage: String?,
    @ColumnInfo(name = "user_id")
    val userId: String?,
    @ColumnInfo(name = "user_name")
    val userName: String?,
    val image: String?,
    @ColumnInfo(name = "likes_count")
    val likesCount: Long,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Long,
    @ColumnInfo(name = "is_liked")
    val isLiked: Boolean,
    @ColumnInfo(name = "date_millis")
    val dateMillis: Long?,
    val ordering: Long,
    @ColumnInfo(name = "updated_at_millis")
    val updatedAtMillis: Long,
)

@Entity(tableName = "comment_cache")
data class CommentCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val cacheKey: String,
    val scope: String,
    @ColumnInfo(name = "comment_id")
    val commentId: Long,
    @ColumnInfo(name = "post_id")
    val postId: Long,
    @ColumnInfo(name = "user_id")
    val userId: String?,
    @ColumnInfo(name = "user_image")
    val userImage: String?,
    @ColumnInfo(name = "user_name")
    val userName: String?,
    val content: String,
    @ColumnInfo(name = "date_millis")
    val dateMillis: Long?,
    @ColumnInfo(name = "updated_at_millis")
    val updatedAtMillis: Long,
)

@Entity(tableName = "community_cache")
data class CommunityCacheEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String?,
    val image: String?,
    @ColumnInfo(name = "subscribers_count")
    val subscribersCount: Long,
    @ColumnInfo(name = "owner_id")
    val ownerId: String?,
    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long?,
    @ColumnInfo(name = "updated_at_millis")
    val updatedAtMillis: Long,
)

@Entity(tableName = "community_search")
data class CommunitySearchMetadataEntity(
    @PrimaryKey
    val key: String,
    @ColumnInfo(name = "updated_at_millis")
    val updatedAtMillis: Long,
)

@Entity(
    tableName = "community_search_entry",
    primaryKeys = ["key", "position"],
)
data class CommunitySearchEntryEntity(
    val key: String,
    @ColumnInfo(name = "community_id")
    val communityId: Long,
    val position: Long,
)
