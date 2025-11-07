package com.edufelip.finn.shared.data.local.room

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun RoomDatabase.Builder<FinnCacheDatabase>.configurePlatformDriver(): RoomDatabase.Builder<FinnCacheDatabase> =
    setDriver(BundledSQLiteDriver())
