package com.edufelip.finn.shared.data.local.room

import androidx.room.Room
import androidx.room.RoomDatabase
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun finnDatabaseBuilder(fileName: String = "finn.db"): RoomDatabase.Builder<FinnCacheDatabase> {
    val dbPath: Path = Paths.get(System.getProperty("user.home"), ".finn", fileName)
    Files.createDirectories(dbPath.parent)
    return Room.databaseBuilder(
        FinnCacheDatabase::class.java,
        dbPath.toAbsolutePath().toString(),
    )
}
