package com.edufelip.finn.shared.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PostCacheEntity::class,
        CommentCacheEntity::class,
        CommunityCacheEntity::class,
        CommunitySearchMetadataEntity::class,
        CommunitySearchEntryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class FinnCacheDatabase : RoomDatabase() {
    abstract fun postCacheDao(): PostCacheDao
    abstract fun commentCacheDao(): CommentCacheDao
    abstract fun communityCacheDao(): CommunityCacheDao
    abstract fun communitySearchDao(): CommunitySearchDao
}
