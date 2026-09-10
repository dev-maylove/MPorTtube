package com.mporttube.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos ORDER BY createdAt DESC, title COLLATE NOCASE")
    fun observeAll(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun get(id: String): VideoEntity?
}

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clear()

    @Query("DELETE FROM history WHERE videoId = :id")
    suspend fun delete(id: String)

    @Query("""
        SELECT v.id, v.title, v.url, v.thumbnailUrl, v.durationMs,
               h.positionMs, h.watchedAt
        FROM history h
        INNER JOIN videos v ON v.id = h.videoId
        ORDER BY h.watchedAt DESC
    """)
    fun observeItems(): Flow<List<HistoryItem>>
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE videoId = :id")
    suspend fun remove(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE videoId = :id)")
    fun observeFavorite(id: String): Flow<Boolean>

    @Query("""
        SELECT v.id, v.title, v.url, v.thumbnailUrl, v.durationMs
        FROM favorites f
        INNER JOIN videos v ON v.id = f.videoId
        ORDER BY f.createdAt DESC
    """)
    fun observeItems(): Flow<List<FavoriteItem>>
}

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PlaylistEntity>>

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVideo(item: PlaylistVideoEntity)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun removeVideo(playlistId: String, videoId: String)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId")
    suspend fun clearVideos(playlistId: String)

    @Query("""
        SELECT v.* FROM playlist_videos pv
        INNER JOIN videos v ON v.id = pv.videoId
        WHERE pv.playlistId = :playlistId
        ORDER BY pv.position ASC
    """)
    fun observeVideos(playlistId: String): Flow<List<VideoEntity>>

    @Query("SELECT COUNT(*) FROM playlist_videos WHERE playlistId = :playlistId")
    suspend fun count(playlistId: String): Int
}

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DownloadEntity)

    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE systemDownloadId = :id LIMIT 1")
    suspend fun bySystemId(id: Long): DownloadEntity?

    @Query("""
        UPDATE downloads
        SET status = :status,
            progress = :progress,
            localUri = :localUri,
            reason = :reason
        WHERE id = :id
    """)
    suspend fun update(
        id: String,
        status: String,
        progress: Int,
        localUri: String?,
        reason: Int
    )

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun delete(id: String)
}
