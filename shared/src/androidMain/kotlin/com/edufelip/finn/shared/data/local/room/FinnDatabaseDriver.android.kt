package com.edufelip.finn.shared.data.local.room

import androidx.room.RoomDatabase

actual fun RoomDatabase.Builder<FinnCacheDatabase>.configurePlatformDriver(): RoomDatabase.Builder<FinnCacheDatabase> =
    this
