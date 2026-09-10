@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mporttube.ui.screen

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.mporttube.data.local.VideoEntity
import com.mporttube.ui.viewmodel.DownloadViewModel
import com.mporttube.ui.viewmodel.FavoriteViewModel
import com.mporttube.ui.viewmodel.HistoryViewModel
import com.mporttube.ui.viewmodel.HomeViewModel
import com.mporttube.ui.viewmodel.PlayerViewModel
import com.mporttube.ui.viewmodel.PlaylistViewModel
import java.util.UUID

@Composable
fun HomeScreen(
    openPlayer: () -> Unit,
    openHistory: () -> Unit,
    openFavorites: () -> Unit,
    openPlaylists: () -> Unit,
    openDownloads: () -> Unit,
    openMusic: () -> Unit = {},
    openSearch: () -> Unit = {},
    openQueue: () -> Unit = {},
    openLibrary: () -> Unit = {},
    openSettings: () -> Unit = {},
    vm: HomeViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel(),
    downloads: DownloadViewModel = hiltViewModel()
) {
    val videos by vm.items.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val localPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            vm.addLocalVideo(
                VideoEntity(
                    id = "local_${UUID.randomUUID()}",
                    title = displayName(context, uri.toString()),
                    url = uri.toString(),
                    source = "LOCAL"
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("MPorTtube", fontWeight = FontWeight.Bold)
                        Text(
                            "Premium Video Hub",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {
                    TextButton(onClick = openMusic) { Text("Music") }
                    TextButton(onClick = openSearch) { Text("Search") }
                    TextButton(onClick = openSettings) { Text("Settings") }
                    TextButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, "MPorTtube - Premium media player")
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Share MPorTtube"))
                    }) { Text("Share") }
                    TextButton(onClick = { localPicker.launch(arrayOf("video/*", "audio/*")) }) { Text("Add local") }
                }
            )
        },
        bottomBar = { MiniPlayer(openPlayer) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Play everything, anywhere",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Media3 • Room • Queue • Downloads • Local files",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = openHistory, label = { Text("History") })
                    AssistChip(onClick = openFavorites, label = { Text("Favorites") })
                    AssistChip(onClick = openPlaylists, label = { Text("Playlists") })
                    AssistChip(onClick = openDownloads, label = { Text("Downloads") })
                }
            }

            item {
                Text("Quick access", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = openSearch, label = { Text("Search") })
                    AssistChip(onClick = openQueue, label = { Text("Queue") })
                    AssistChip(onClick = openLibrary, label = { Text("Library") })
                    AssistChip(onClick = openSettings, label = { Text("Settings") })
                }
            }

            item {
                Text(
                    "Video Library",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(videos, key = { it.id }) { video ->
                VideoRow(
                    video = video,
                    onPlay = {
                        player.queue(videos, videos.indexOf(video))
                        openPlayer()
                    },
                    onDownload = { downloads.enqueue(video) }
                )
            }
        }
    }
}

@Composable
private fun VideoRow(
    video: VideoEntity,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
    favorites: FavoriteViewModel = hiltViewModel(),
    playlists: PlaylistViewModel = hiltViewModel()
) {
    val favoriteFlow = remember(video.id) {
        favorites.favorite(video.id)
    }
    val favorite by favoriteFlow.collectAsStateWithLifecycle()

    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (video.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .size(108.dp, 68.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(Modifier.width(10.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(
                    video.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    video.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row {
                    TextButton(onClick = onPlay) { Text("Play") }
                    TextButton(
                        onClick = {
                            favorites.toggle(video.id, favorite)
                        }
                    ) {
                        Text(if (favorite) "Unfavorite" else "Favorite")
                    }
                    if (video.source != "LOCAL") {
                        TextButton(onClick = onDownload) { Text("Download") }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    openPlayer: () -> Unit,
    vm: PlayerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val current = state.current ?: return

    Surface(tonalElevation = 8.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = openPlayer)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    current.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (state.isPlaying) "Now playing" else "Paused",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TextButton(onClick = vm::previous) { Text("Prev") }
            TextButton(onClick = vm::toggle) {
                Text(if (state.isPlaying) "Pause" else "Play")
            }
            TextButton(onClick = vm::next) { Text("Next") }
        }
    }
}

@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    vm: PlayerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val current = state.current
    val context = LocalContext.current
    val activity = context as? Activity
    var fullscreen by remember { mutableStateOf(false) }

    fun setFullscreen(enabled: Boolean) {
        val window = activity?.window ?: return
        WindowCompat.setDecorFitsSystemWindows(window, !enabled)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if (enabled) {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                show(WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { setFullscreen(false) }
    }

    Scaffold(
        topBar = {
            if (!fullscreen) {
                TopAppBar(
                    title = { Text(current?.title ?: "Player") },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back") }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(if (fullscreen) PaddingValues(0.dp) else padding)
                .padding(if (fullscreen) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AndroidView(
                modifier = if (fullscreen) {
                    Modifier.fillMaxWidth().weight(1f)
                } else {
                    Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                },
                factory = { context ->
                    PlayerView(context).apply {
                        useController = true
                        player = vm.exoPlayer
                    }
                },
                update = { it.player = vm.exoPlayer }
            )

            if (!fullscreen) {
                Text(current?.title ?: "Select a video from Home")

                val duration = state.durationMs.coerceAtLeast(1L)
                val progress = (state.positionMs.toFloat() / duration.toFloat())
                    .coerceIn(0f, 1f)
                Slider(
                    value = progress,
                    onValueChange = { vm.seek((it * duration).toLong()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${state.positionMs / 1000}s / ${state.durationMs / 1000}s")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = vm::previous) { Text("Previous") }
                    Button(onClick = vm::toggle) {
                        Text(if (state.isPlaying) "Pause" else "Play")
                    }
                    OutlinedButton(onClick = vm::next) { Text("Next") }
                    OutlinedButton(
                        onClick = {
                            fullscreen = true
                            setFullscreen(true)
                        }
                    ) { Text("Fullscreen") }
                }

                Text(
                    "Queue ${if (state.queueIndex >= 0) state.queueIndex + 1 else 0}/${state.queue.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(state.queue, key = { it.id }) { item ->
                        val selected = item.id == current?.id
                        ListItem(
                            modifier = Modifier.clickable {
                                vm.queue(
                                    state.queue,
                                    state.queue.indexOf(item)
                                )
                            },
                            headlineContent = { Text(item.title) },
                            supportingContent = {
                                Text(if (selected) "Playing" else item.source)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            } else {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        fullscreen = false
                        setFullscreen(false)
                    }
                ) { Text("Exit Fullscreen") }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    openPlayer: () -> Unit,
    vm: HistoryViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    TextButton(onClick = vm::clear) { Text("Clear") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(items, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    supportingContent = {
                        Text("Resume at ${item.positionMs / 1000}s")
                    },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                player.play(
                                    VideoEntity(
                                        item.id,
                                        item.title,
                                        item.url,
                                        item.thumbnailUrl,
                                        item.durationMs
                                    ),
                                    item.positionMs
                                )
                                openPlayer()
                            }
                        ) { Text("Resume") }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    openPlayer: () -> Unit,
    vm: FavoriteViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(items, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text("Saved favorite") },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                player.play(
                                    VideoEntity(
                                        item.id,
                                        item.title,
                                        item.url,
                                        item.thumbnailUrl,
                                        item.durationMs
                                    )
                                )
                                openPlayer()
                            }
                        ) { Text("Play") }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PlaylistsScreen(
    onBack: () -> Unit,
    openPlaylist: (String) -> Unit,
    vm: PlaylistViewModel = hiltViewModel()
) {
    val playlists by vm.playlists.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playlists") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("New playlist") }
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        vm.create(name)
                        name = ""
                    }
                ) { Text("Add") }
            }

            LazyColumn {
                items(playlists, key = { it.id }) { list ->
                    ListItem(
                        modifier = Modifier.clickable {
                            openPlaylist(list.id)
                        },
                        headlineContent = { Text(list.name) },
                        supportingContent = {
                            Text("Tap to open playlist")
                        },
                        trailingContent = {
                            TextButton(onClick = { vm.delete(list.id) }) {
                                Text("Delete")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    playlistId: String,
    onBack: () -> Unit,
    openPlayer: () -> Unit,
    vm: PlaylistViewModel = hiltViewModel(),
    home: HomeViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel()
) {
    val videos by vm.videos(playlistId).collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val library by home.items.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playlist") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Playlist videos",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(videos, key = { it.id }) { video ->
                ListItem(
                    headlineContent = { Text(video.title) },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                player.queue(
                                    videos,
                                    videos.indexOf(video)
                                )
                                openPlayer()
                            }
                        ) { Text("Play") }
                    }
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Add from library",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(library, key = { "lib_${it.id}" }) { video ->
                ListItem(
                    headlineContent = { Text(video.title) },
                    trailingContent = {
                        TextButton(
                            onClick = { vm.add(playlistId, video.id) }
                        ) { Text("Add") }
                    }
                )
            }
        }
    }
}

@Composable
fun DownloadsScreen(
    onBack: () -> Unit,
    vm: DownloadViewModel = hiltViewModel()
) {
    val items by vm.downloads.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Downloads") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    TextButton(onClick = vm::refresh) { Text("Refresh") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items, key = { it.id }) { item ->
                ElevatedCard {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            item.title,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(item.status)
                        LinearProgressIndicator(
                            progress = {
                                item.progress.coerceIn(0, 100) / 100f
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("${item.progress}%")
                        Row {
                            if (
                                item.status == "RUNNING" ||
                                item.status == "PENDING" ||
                                item.status == "PAUSED"
                            ) {
                                TextButton(
                                    onClick = { vm.cancel(item.id) }
                                ) { Text("Cancel") }
                            }
                            TextButton(
                                onClick = { vm.remove(item.id) }
                            ) { Text("Remove") }
                        }
                    }
                }
            }
        }
    }
}

private fun displayName(context: Context, uriString: String): String {
    val uri = android.net.Uri.parse(uriString)
    context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            return cursor.getString(index)
        }
    }
    return "Local media"
}

/** Local music browser backed by Android MediaStore. No network or scraping is used. */
@Composable
fun MusicScreen(onBack: () -> Unit, onOpenPlayer: () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasAudioPermission(context)) }
    var tracks by remember { mutableStateOf(emptyList<VideoEntity>()) }
    val playerVm: PlayerViewModel = hiltViewModel()

    val permissionLauncher = rememberLauncherForActivityResult(
        if (android.os.Build.VERSION.SDK_INT >= 33)
            ActivityResultContracts.RequestPermission()
        else ActivityResultContracts.RequestPermission()
    ) { ok ->
        granted = ok
        if (ok) tracks = loadDeviceMusic(context)
    }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    androidx.compose.runtime.LaunchedEffect(granted) {
        if (granted) tracks = loadDeviceMusic(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Music"); Text("Local device library", style = MaterialTheme.typography.labelSmall) } },
                navigationIcon = { IconButton(onClick = onBack) { Text("‹", style = MaterialTheme.typography.headlineMedium) } },
                actions = {
                    TextButton(onClick = {
                        if (android.os.Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }) { Text("Notifications") }
                    TextButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, "MPorTtube - Premium local music and media player")
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Share MPorTtube"))
                    }) { Text("Share") }
                }
            )
        },
        bottomBar = { MiniPlayer(onOpenPlayer) }
    ) { padding ->
        if (!granted) {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Allow music access", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                Text("MPorTtube needs storage/media permission to read music already stored on your device.")
                Spacer(Modifier.height(18.dp))
                Button(onClick = {
                    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE
                    permissionLauncher.launch(permission)
                }) { Text("Allow Music Access") }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Songs on this device", style = MaterialTheme.typography.titleLarge) }
                if (tracks.isEmpty()) item { Text("No music found in device storage.") }
                items(tracks, key = { it.id }) { track ->
                    ElevatedCard(Modifier.fillMaxWidth().clickable {
                        playerVm.queue(tracks, tracks.indexOf(track))
                        onOpenPlayer()
                    }) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(track.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(track.source, style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { playerVm.queue(tracks, tracks.indexOf(track)); onOpenPlayer() }) { Text("Play") }
                        }
                    }
                }
            }
        }
    }
}

private fun hasAudioPermission(context: Context): Boolean {
    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE
    return androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun loadDeviceMusic(context: Context): List<VideoEntity> {
    val result = mutableListOf<VideoEntity>()
    val collection = if (android.os.Build.VERSION.SDK_INT >= 29) android.provider.MediaStore.Audio.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL) else android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(android.provider.MediaStore.Audio.Media._ID, android.provider.MediaStore.Audio.Media.TITLE, android.provider.MediaStore.Audio.Media.ARTIST)
    context.contentResolver.query(collection, projection, android.provider.MediaStore.Audio.Media.IS_MUSIC + "!=0", null, android.provider.MediaStore.Audio.Media.TITLE + " ASC")?.use { c ->
        val idCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID)
        val titleCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
        val artistCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
        while (c.moveToNext()) {
            val id = c.getLong(idCol)
            val uri = android.content.ContentUris.withAppendedId(collection, id)
            val title = c.getString(titleCol) ?: "Unknown title"
            val artist = c.getString(artistCol) ?: "Unknown artist"
            result += VideoEntity(id = "music_$id", title = title, url = uri.toString(), source = artist)
        }
    }
    return result
}


/** Android 13+ uses READ_MEDIA_VIDEO; Android 12 and below use READ_EXTERNAL_STORAGE. */
@Composable
fun LocalVideoScreen(onBack: () -> Unit, onOpenPlayer: () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasVideoPermission(context)) }
    var videos by remember { mutableStateOf(emptyList<VideoEntity>()) }
    val playerVm: PlayerViewModel = hiltViewModel()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) videos = loadDeviceVideos(context)
    }
    androidx.compose.runtime.LaunchedEffect(granted) { if (granted) videos = loadDeviceVideos(context) }
    Scaffold(
        topBar = { TopAppBar(title = { Column { Text("Local Videos"); Text("Device gallery", style = MaterialTheme.typography.labelSmall) } }, navigationIcon = { IconButton(onClick = onBack) { Text("‹", style = MaterialTheme.typography.headlineMedium) } }) },
        bottomBar = { MiniPlayer(onOpenPlayer) }
    ) { padding ->
        if (!granted) {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Allow video access", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                Text("MPorTtube needs media permission to read videos already stored in your device gallery.")
                Spacer(Modifier.height(18.dp))
                Button(onClick = {
                    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_VIDEO else android.Manifest.permission.READ_EXTERNAL_STORAGE
                    launcher.launch(permission)
                }) { Text("Allow Video Access") }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Videos on this device", style = MaterialTheme.typography.titleLarge) }
                if (videos.isEmpty()) item { Text("No local videos found.") }
                items(videos, key = { it.id }) { video ->
                    ElevatedCard(Modifier.fillMaxWidth().clickable { playerVm.queue(videos, videos.indexOf(video)); onOpenPlayer() }) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(video.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(video.source, style = MaterialTheme.typography.bodySmall) }
                            TextButton(onClick = { playerVm.queue(videos, videos.indexOf(video)); onOpenPlayer() }) { Text("Play") }
                        }
                    }
                }
            }
        }
    }
}

private fun hasVideoPermission(context: Context): Boolean {
    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_VIDEO else android.Manifest.permission.READ_EXTERNAL_STORAGE
    return androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun loadDeviceVideos(context: Context): List<VideoEntity> {
    val result = mutableListOf<VideoEntity>()
    val collection = if (android.os.Build.VERSION.SDK_INT >= 29) android.provider.MediaStore.Video.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL) else android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(android.provider.MediaStore.Video.Media._ID, android.provider.MediaStore.Video.Media.TITLE, android.provider.MediaStore.Video.Media.DISPLAY_NAME)
    context.contentResolver.query(collection, projection, null, null, android.provider.MediaStore.Video.Media.DATE_ADDED + " DESC")?.use { c ->
        val idCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media._ID)
        val titleCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.TITLE)
        val nameCol = c.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.DISPLAY_NAME)
        while (c.moveToNext()) {
            val id = c.getLong(idCol)
            val uri = android.content.ContentUris.withAppendedId(collection, id)
            val title = c.getString(titleCol)?.takeIf { it.isNotBlank() } ?: c.getString(nameCol) ?: "Local video"
            result += VideoEntity(id = "video_$id", title = title, url = uri.toString(), source = "LOCAL VIDEO")
        }
    }
    return result
}
