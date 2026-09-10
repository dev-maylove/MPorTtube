package com.mporttube.player

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mporttube.data.local.VideoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class PlayerUiState(
    val current: VideoEntity? = null,
    val queue: List<VideoEntity> = emptyList(),
    val queueIndex: Int = -1,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackState: Int = Player.STATE_IDLE
)

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val queue = mutableListOf<VideoEntity>()
    private val prefs = context.getSharedPreferences("mporttube_settings", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = publish()
            override fun onPlaybackStateChanged(playbackState: Int) = publish()
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && !prefs.getBoolean("autoplay", true)) player.pause()
                publish()
            }
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) = publish()
        })

        scope.launch {
            while (isActive) {
                if (_state.value.current != null) publish()
                delay(500L)
            }
        }
    }

    private fun publish() {
        val index = player.currentMediaItemIndex
            .takeIf { it in queue.indices }
            ?: -1

        val current = if (index >= 0) queue[index] else null
        val duration = player.duration
            .takeIf { it != C.TIME_UNSET }
            ?.coerceAtLeast(0L)
            ?: 0L

        _state.value = PlayerUiState(
            current = current,
            queue = queue.toList(),
            queueIndex = index,
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = duration,
            playbackState = player.playbackState
        )
    }

    private fun ensurePlaybackService() {
        if (!prefs.getBoolean("background_playback", true)) return
        val intent = Intent(context, PlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun mediaItem(video: VideoEntity): MediaItem =
        MediaItem.Builder()
            .setMediaId(video.id)
            .setUri(video.url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(video.title)
                    .setArtworkUri(
                        video.thumbnailUrl
                            .takeIf { it.isNotBlank() }
                            ?.let { android.net.Uri.parse(it) }
                    )
                    .build()
            )
            .build()

    fun play(video: VideoEntity, startPosition: Long = 0L) {
        ensurePlaybackService()
        queue.clear()
        queue += video
        player.setMediaItem(mediaItem(video))
        player.prepare()
        player.seekTo(startPosition.coerceAtLeast(0L))
        player.play()
        publish()
    }

    fun setQueue(videos: List<VideoEntity>, startIndex: Int = 0) {
        if (videos.isEmpty()) return
        ensurePlaybackService()
        queue.clear()
        queue += videos
        val index = startIndex.coerceIn(0, videos.lastIndex)
        player.setMediaItems(videos.map(::mediaItem), index, 0L)
        player.prepare()
        player.play()
        publish()
    }

    fun toggle() {
        if (player.isPlaying) player.pause() else player.play()
        publish()
    }

    fun seekTo(position: Long) {
        player.seekTo(position.coerceAtLeast(0L))
        publish()
    }

    fun next() {
        if (player.hasNextMediaItem()) player.seekToNextMediaItem()
        publish()
    }

    fun previous() {
        if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem()
        else player.seekTo(0L)
        publish()
    }

    fun clearPlaybackData() {
        player.pause()
        player.stop()
        player.clearMediaItems()
        queue.clear()
        _state.value = PlayerUiState()
    }

    fun release() {
        player.release()
    }
}
