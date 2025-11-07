package com.edufelip.finn.shared.data.local.room

import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers

fun RoomDatabase.Builder<FinnCacheDatabase>.buildFinnDatabase(): FinnCacheDatabase =
    configurePlatformDriver()
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()

expect fun RoomDatabase.Builder<FinnCacheDatabase>.configurePlatformDriver(): RoomDatabase.Builder<FinnCacheDatabase>
