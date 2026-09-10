package com.mporttube.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mporttube.data.local.VideoEntity
import com.mporttube.data.repository.DownloadRepository
import com.mporttube.data.repository.FavoriteRepository
import com.mporttube.data.repository.HistoryRepository
import com.mporttube.data.repository.PlaylistRepository
import com.mporttube.data.repository.VideoRepository
import com.mporttube.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videos: VideoRepository
) : ViewModel() {
    val items = videos.videos().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    init {
        viewModelScope.launch { videos.seedIfEmpty() }
    }

    fun addLocalVideo(video: VideoEntity) {
        viewModelScope.launch { videos.save(video) }
    }
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val manager: PlayerManager,
    private val history: HistoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val state = manager.state
    val exoPlayer get() = manager.player

    init {
        viewModelScope.launch {
            var lastVideoId: String? = null
            var lastSavedAt = 0L

            state.collect { playerState ->
                if (context.getSharedPreferences("mporttube_settings", Context.MODE_PRIVATE).getBoolean("private_mode", false)) return@collect
                val videoId = playerState.current?.id ?: return@collect
                val now = System.currentTimeMillis()

                if (
                    playerState.positionMs > 0L &&
                    (
                        videoId != lastVideoId ||
                        now - lastSavedAt >= 5_000L
                    )
                ) {
                    history.save(videoId, playerState.positionMs)
                    lastVideoId = videoId
                    lastSavedAt = now
                }
            }
        }
    }

    fun play(video: VideoEntity, position: Long = 0L) =
        manager.play(video, position)

    fun queue(list: List<VideoEntity>, index: Int = 0) =
        manager.setQueue(list, index)

    fun toggle() = manager.toggle()
    fun seek(pos: Long) = manager.seekTo(pos)
    fun next() = manager.next()
    fun previous() = manager.previous()
    fun clearPlaybackData() = manager.clearPlaybackData()

    fun saveHistory() {
        if (context.getSharedPreferences("mporttube_settings", Context.MODE_PRIVATE).getBoolean("private_mode", false)) return
        val current = state.value.current ?: return
        viewModelScope.launch {
            history.save(current.id, manager.player.currentPosition)
        }
    }
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: HistoryRepository
) : ViewModel() {
    val items = repo.items().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun clear() = viewModelScope.launch { repo.clear() }
    fun delete(id: String) = viewModelScope.launch { repo.delete(id) }
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: FavoriteRepository
) : ViewModel() {
    val items = repo.items().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun favorite(id: String): StateFlow<Boolean> =
        repo.isFavorite(id).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false
        )

    fun toggle(id: String, current: Boolean) =
        viewModelScope.launch { repo.toggle(id, current) }
}

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val repo: PlaylistRepository
) : ViewModel() {
    val playlists = repo.playlists().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun create(name: String) = viewModelScope.launch { repo.create(name) }
    fun delete(id: String) = viewModelScope.launch { repo.delete(id) }
    fun add(playlistId: String, videoId: String) =
        viewModelScope.launch { repo.add(playlistId, videoId) }

    fun videos(id: String) = repo.videos(id)
}

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val repo: DownloadRepository
) : ViewModel() {
    val downloads = repo.downloads().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    init {
        viewModelScope.launch {
            while (isActive) {
                runCatching { repo.refresh() }
                delay(1_000L)
            }
        }
    }

    fun enqueue(video: VideoEntity) =
        viewModelScope.launch { repo.enqueue(video) }

    fun refresh() = viewModelScope.launch { repo.refresh() }
    fun cancel(id: String) = viewModelScope.launch { repo.cancel(id) }
    fun remove(id: String) = viewModelScope.launch { repo.remove(id) }
}
