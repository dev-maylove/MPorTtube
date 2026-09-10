package com.mporttube.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String = "",
    val durationMs: Long = 0L,
    val source: String = "REMOTE",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val videoId: String,
    val positionMs: Long,
    val watchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val videoId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_videos",
    primaryKeys = ["playlistId", "videoId"]
)
data class PlaylistVideoEntity(
    val playlistId: String,
    val videoId: String,
    val position: Int
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val systemDownloadId: Long = -1L,
    val videoId: String,
    val title: String,
    val sourceUrl: String,
    val localUri: String? = null,
    val status: String = "QUEUED",
    val progress: Int = 0,
    val reason: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val durationMs: Long,
    val positionMs: Long,
    val watchedAt: Long
)

data class FavoriteItem(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val durationMs: Long
)
