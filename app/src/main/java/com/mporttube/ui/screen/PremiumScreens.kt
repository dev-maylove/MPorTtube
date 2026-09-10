package com.mporttube.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mporttube.R
import com.mporttube.data.local.VideoEntity
import com.mporttube.ui.viewmodel.HomeViewModel
import com.mporttube.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun PremiumSplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 7_000),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(7_000L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "MPorTtube logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(24.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFF27D8FF),
                trackColor = Color(0xFF15213A)
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "LOADING SYSTEM.... ${(progress * 100).toInt()}%",
                color = Color(0xFFBEEBFF),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PremiumHomeScreen(
    onOpenPlayer: () -> Unit,
    onSearch: () -> Unit,
    onQueue: () -> Unit,
    onLibrary: () -> Unit,
    onSettings: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel()
) {
    val videos by vm.items.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onSearch,
                    icon = { Text("⌕") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLibrary,
                    icon = { Text("▣") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onSettings,
                    icon = { Text("⚙") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "MPorTtube logo",
                        modifier = Modifier.size(58.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "MPorTtube",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Play Everything, Anywhere",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    AssistChip(
                        onClick = onSearch,
                        label = { Text("⌕ Search") }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF20365C),
                                    Color(0xFF2B1A46),
                                    Color(0xFF0B1422)
                                )
                            )
                        ).padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {
                            Text(
                                "Premium Player",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Media3 • Queue • History • Downloads",
                                color = Color(0xFFD6DDF0)
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = onQueue, label = { Text("Queue") })
                    AssistChip(onClick = onLibrary, label = { Text("Library") })
                }
            }

            item {
                Text(
                    "Untuk Anda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(videos, key = { it.id }) { video ->
                PremiumVideoCard(
                    video = video,
                    onPlay = {
                        player.queue(videos, videos.indexOf(video))
                        onOpenPlayer()
                    }
                )
            }
        }
    }
}

@Composable
private fun PremiumVideoCard(
    video: VideoEntity,
    onPlay: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onPlay)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (video.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .size(116.dp, 72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(
                    video.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    video.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            FilledTonalButton(onClick = onPlay) {
                Text("Play")
            }
        }
    }
}

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenPlayer: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
    player: PlayerViewModel = hiltViewModel()
) {
    val videos by vm.items.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(videos, query) {
        if (query.isBlank()) videos
        else videos.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.source.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search video or source") }
            )

            if (filtered.isEmpty()) {
                Text("No matching videos.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered, key = { it.id }) { video ->
                        PremiumVideoCard(
                            video = video,
                            onPlay = {
                                player.queue(filtered, filtered.indexOf(video))
                                onOpenPlayer()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QueueScreen(
    onBack: () -> Unit,
    onOpenPlayer: () -> Unit,
    vm: PlayerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Queue") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        if (state.queue.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Queue is empty. Play a video from Home first.")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(state.queue, key = { it.id }) { item ->
                    val selected = item.id == state.current?.id
                    ListItem(
                        modifier = Modifier.clickable {
                            vm.queue(state.queue, state.queue.indexOf(item))
                            onOpenPlayer()
                        },
                        headlineContent = { Text(item.title) },
                        supportingContent = {
                            Text(if (selected) "Now playing" else item.source)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun LibraryHubScreen(
    onBack: () -> Unit,
    openHistory: () -> Unit,
    openFavorites: () -> Unit,
    openPlaylists: () -> Unit,
    openDownloads: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LibraryButton("History", openHistory)
            LibraryButton("Favorites", openFavorites)
            LibraryButton("Playlists", openPlaylists)
            LibraryButton("Downloads", openDownloads)
        }
    }
}

@Composable
private fun LibraryButton(label: String, action: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = action)
    ) {
        Text(
            label,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var autoplay by rememberSaveable { mutableStateOf(true) }
    var backgroundPlayback by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(
                "Playback",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            ListItem(
                headlineContent = { Text("Autoplay") },
                trailingContent = {
                    Switch(checked = autoplay, onCheckedChange = { autoplay = it })
                }
            )
            ListItem(
                headlineContent = { Text("Background playback") },
                trailingContent = {
                    Switch(
                        checked = backgroundPlayback,
                        onCheckedChange = { backgroundPlayback = it }
                    )
                }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "MPorTtube V5.2 • synchronized core + premium UI",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
