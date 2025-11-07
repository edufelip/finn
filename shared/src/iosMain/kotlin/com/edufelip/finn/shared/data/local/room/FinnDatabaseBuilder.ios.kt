package com.edufelip.finn.shared.data.local.room

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSSearchPathDomainMask
import platform.Foundation.NSURL

fun finnDatabaseBuilder(fileName: String = "finn.db"): RoomDatabase.Builder<FinnCacheDatabase> {
    val manager = NSFileManager.defaultManager
    val directory = manager.URLsForDirectory(
        directory = NSSearchPathDirectory.NSDocumentDirectory,
        inDomains = NSSearchPathDomainMask.NSUserDomainMask,
    ).firstOrNull() as? NSURL
    val dbUrl = directory?.URLByAppendingPathComponent(fileName)
    val path = dbUrl?.path ?: fileName
    return Room.databaseBuilder(
        klass = FinnCacheDatabase::class,
        name = path,
    )
}
