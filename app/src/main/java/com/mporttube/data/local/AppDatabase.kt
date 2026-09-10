package com.mporttube.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        VideoEntity::class,
        HistoryEntity::class,
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistVideoEntity::class,
        DownloadEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun downloadDao(): DownloadDao
}
