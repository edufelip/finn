package com.edufelip.finn.shared.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun finnDatabaseBuilder(
    context: Context,
    fileName: String = "finn.db",
): RoomDatabase.Builder<FinnCacheDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(fileName).apply {
        parentFile?.mkdirs()
    }
    return Room.databaseBuilder(
        context,
        FinnCacheDatabase::class.java,
        dbFile.absolutePath,
    )
}
