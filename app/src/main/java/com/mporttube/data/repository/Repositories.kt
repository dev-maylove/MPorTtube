package com.mporttube.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.mporttube.data.local.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val dao: VideoDao
) {
    private val defaults = listOf(
        VideoEntity(
            id = "bbb",
            title = "Big Buck Bunny",
            url = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg",
            source = "SAMPLE"
        ),
        VideoEntity(
            id = "elephants",
            title = "Elephants Dream",
            url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            source = "SAMPLE"
        ),
        VideoEntity(
            id = "sintel",
            title = "Sintel",
            url = "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            source = "SAMPLE"
        )
    )

    fun videos(): Flow<List<VideoEntity>> = dao.observeAll()

    suspend fun seedIfEmpty() {
        if (dao.observeAll().first().isEmpty()) dao.upsertAll(defaults)
    }

    suspend fun save(video: VideoEntity) = dao.upsert(video)
    suspend fun get(id: String) = dao.get(id)
}

@Singleton
class HistoryRepository @Inject constructor(
    private val dao: HistoryDao
) {
    fun items(): Flow<List<HistoryItem>> = dao.observeItems()

    suspend fun save(videoId: String, position: Long) {
        dao.upsert(
            HistoryEntity(
                videoId = videoId,
                positionMs = position.coerceAtLeast(0L)
            )
        )
    }

    suspend fun clear() = dao.clear()
    suspend fun delete(id: String) = dao.delete(id)
}

@Singleton
class FavoriteRepository @Inject constructor(
    private val dao: FavoriteDao
) {
    fun items(): Flow<List<FavoriteItem>> = dao.observeItems()
    fun isFavorite(id: String): Flow<Boolean> = dao.observeFavorite(id)

    suspend fun toggle(id: String, current: Boolean) {
        if (current) dao.remove(id) else dao.add(FavoriteEntity(videoId = id))
    }
}

@Singleton
class PlaylistRepository @Inject constructor(
    private val dao: PlaylistDao
) {
    fun playlists(): Flow<List<PlaylistEntity>> = dao.observeAll()
    fun videos(id: String): Flow<List<VideoEntity>> = dao.observeVideos(id)

    suspend fun create(name: String) {
        val clean = name.trim()
        if (clean.isNotEmpty()) dao.upsert(PlaylistEntity(name = clean))
    }

    suspend fun delete(id: String) {
        dao.clearVideos(id)
        dao.delete(id)
    }

    suspend fun add(playlistId: String, videoId: String) {
        dao.addVideo(
            PlaylistVideoEntity(
                playlistId = playlistId,
                videoId = videoId,
                position = dao.count(playlistId)
            )
        )
    }

    suspend fun remove(playlistId: String, videoId: String) =
        dao.removeVideo(playlistId, videoId)
}

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: DownloadDao
) {
    private val manager: DownloadManager
        get() = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun downloads(): Flow<List<DownloadEntity>> = dao.observeAll()

    suspend fun enqueue(video: VideoEntity) {
        val request = DownloadManager.Request(Uri.parse(video.url))
            .setTitle(video.title)
            .setDescription("Downloading with MPorTtube")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_MOVIES,
                "MPorTtube_${video.id}.mp4"
            )

        val systemId = manager.enqueue(request)
        dao.upsert(
            DownloadEntity(
                id = UUID.randomUUID().toString(),
                systemDownloadId = systemId,
                videoId = video.id,
                title = video.title,
                sourceUrl = video.url,
                status = "PENDING"
            )
        )
    }

    suspend fun refresh() {
        val current = downloads().first()
        current.forEach { item ->
            if (item.systemDownloadId < 0L) return@forEach

            manager.query(
                DownloadManager.Query().setFilterById(item.systemDownloadId)
            ).use { cursor ->
                if (!cursor.moveToFirst()) return@use

                val statusCode = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                )
                val downloaded = cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                    )
                )
                val total = cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_TOTAL_SIZE_BYTES
                    )
                )
                val progress = if (total > 0L) {
                    ((downloaded * 100L) / total).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                val localUri = cursor.getString(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                )
                val reason = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                )

                val status = when (statusCode) {
                    DownloadManager.STATUS_SUCCESSFUL -> "COMPLETED"
                    DownloadManager.STATUS_FAILED -> "FAILED"
                    DownloadManager.STATUS_PAUSED -> "PAUSED"
                    DownloadManager.STATUS_PENDING -> "PENDING"
                    DownloadManager.STATUS_RUNNING -> "RUNNING"
                    else -> "UNKNOWN"
                }

                dao.update(
                    id = item.id,
                    status = status,
                    progress = if (status == "COMPLETED") 100 else progress,
                    localUri = localUri,
                    reason = reason
                )
            }
        }
    }

    suspend fun markCompleted(systemId: Long) {
        val item = dao.bySystemId(systemId) ?: return
        val uri = manager.getUriForDownloadedFile(systemId)?.toString()
        dao.update(
            id = item.id,
            status = "COMPLETED",
            progress = 100,
            localUri = uri,
            reason = 0
        )
    }

    suspend fun cancel(id: String) {
        val item = downloads().first().firstOrNull { it.id == id } ?: return
        if (item.systemDownloadId >= 0L) manager.remove(item.systemDownloadId)
        dao.update(item.id, "CANCELLED", item.progress, item.localUri, 0)
    }

    suspend fun remove(id: String) {
        val item = downloads().first().firstOrNull { it.id == id } ?: return
        if (item.systemDownloadId >= 0L && item.status != "COMPLETED") {
            manager.remove(item.systemDownloadId)
        }
        item.localUri
            ?.takeIf { it.startsWith("file://") }
            ?.let { runCatching { File(Uri.parse(it).path.orEmpty()).delete() } }
        dao.delete(id)
    }
}
