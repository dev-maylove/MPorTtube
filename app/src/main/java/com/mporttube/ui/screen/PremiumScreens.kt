@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mporttube.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.saveable.rememberSaveable
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

    // The logo is kept inside a square viewport and uses ContentScale.Fit.
    // This prevents clipping on tall, short, narrow, and gesture-navigation screens.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val logoSize = minOf(maxWidth * 0.82f, maxHeight * 0.48f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(logoSize),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "MPorTtube logo",
                    modifier = Modifier
                        .size(logoSize),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            }

            Spacer(Modifier.height(26.dp))

            Text(
                text = "MPORTTUBE",
                color = Color(0xFFEAF7FF),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "PREMIUM MEDIA EXPERIENCE",
                color = Color(0xFF7FA8C7),
                fontSize = 10.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(28.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
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
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenDownloads: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { com.mporttube.data.settings.SettingsStore(context.applicationContext) }
    val autoplay by store.autoplay.collectAsState()
    val backgroundPlayback by store.backgroundPlayback.collectAsState()
    val wifiOnly by store.wifiOnly.collectAsState()
    val notifications by store.notifications.collectAsState()
    val highQuality by store.highQuality.collectAsState()
    val dynamicTheme by store.dynamicTheme.collectAsState()
    val privateMode by store.privateMode.collectAsState()
    val downloadQuality by store.downloadQuality.collectAsState()
    val interfaceStyle by store.interfaceStyle.collectAsState()
    val player: PlayerViewModel = hiltViewModel()
    var qualityDialog by rememberSaveable { mutableStateOf(false) }
    var styleDialog by rememberSaveable { mutableStateOf(false) }
    var clearDialog by rememberSaveable { mutableStateOf(false) }

    if (qualityDialog) {
        AlertDialog(
            onDismissRequest = { qualityDialog = false },
            title = { Text("Download quality") },
            text = {
                Column {
                    listOf("Best available", "1080p preferred", "720p balanced", "Data saver").forEach { option ->
                        ListItem(
                            headlineContent = { Text(option) },
                            modifier = Modifier.clickable { store.setDownloadQuality(option); qualityDialog = false },
                            trailingContent = { RadioButton(selected = downloadQuality == option, onClick = { store.setDownloadQuality(option); qualityDialog = false }) }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { qualityDialog = false }) { Text("Close") } }
        )
    }
    if (styleDialog) {
        AlertDialog(
            onDismissRequest = { styleDialog = false },
            title = { Text("Interface style") },
            text = {
                Column {
                    listOf("Premium dark", "Midnight blue", "OLED black").forEach { option ->
                        ListItem(
                            headlineContent = { Text(option) },
                            modifier = Modifier.clickable { store.setInterfaceStyle(option); styleDialog = false },
                            trailingContent = { RadioButton(selected = interfaceStyle == option, onClick = { store.setInterfaceStyle(option); styleDialog = false }) }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { styleDialog = false }) { Text("Close") } }
        )
    }
    if (clearDialog) {
        AlertDialog(
            onDismissRequest = { clearDialog = false },
            title = { Text("Clear playback data?") },
            text = { Text("The current player queue and temporary playback state will be cleared. Your saved favorites and playlists are not removed.") },
            confirmButton = { TextButton(onClick = { player.clearPlaybackData(); clearDialog = false }) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { clearDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Settings", fontWeight = FontWeight.Bold); Text("Personalize your MPorTtube experience", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF101D31))) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Color(0xFF3B8DFF), Color(0xFF6B42D8)))), contentAlignment = Alignment.Center) { Icon(Icons.Filled.AutoAwesome, null, tint = Color.White) }
                        Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text("MPorTtube Premium", fontWeight = FontWeight.Bold); Text("Your preferences are saved automatically", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9EB9D6)) }
                    }
                }
            }
            item { SettingsSectionTitle("Playback", Icons.Filled.PlayCircle) }
            item { SettingsPanel {
                SettingsSwitchRow("Autoplay next", "Continue with the next item in your queue", autoplay, store::setAutoplay)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsSwitchRow("Background playback", "Keep audio playing when the app is not visible", backgroundPlayback, store::setBackgroundPlayback)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsSwitchRow("High quality by default", "Prefer the best available stream quality", highQuality, store::setHighQuality)
            } }
            item { SettingsSectionTitle("Downloads & Network", Icons.Filled.Download) }
            item { SettingsPanel {
                SettingsSwitchRow("Wi‑Fi only downloads", "Avoid mobile data for new downloads", wifiOnly, store::setWifiOnly, Icons.Filled.Wifi)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsActionRow("Download quality", downloadQuality, Icons.Filled.Download) { qualityDialog = true }
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsActionRow("Storage manager", "Review active downloads and local media", Icons.Filled.Storage, onClick = onOpenDownloads)
            } }
            item { SettingsSectionTitle("Appearance", Icons.Filled.DarkMode) }
            item { SettingsPanel {
                SettingsSwitchRow("Dynamic appearance", "Save device-aware appearance preference", dynamicTheme, store::setDynamicTheme, Icons.Filled.DarkMode)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsActionRow("Interface style", interfaceStyle, Icons.Filled.SettingsSuggest) { styleDialog = true }
            } }
            item { SettingsSectionTitle("Privacy & System", Icons.Filled.Security) }
            item { SettingsPanel {
                SettingsSwitchRow("Private session", "Save private-session preference for future playback features", privateMode, store::setPrivateMode, Icons.Filled.Security)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsSwitchRow("Download notifications", "Show progress and completion updates", notifications, store::setNotifications)
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsActionRow("Clear playback data", "Clear current queue and temporary player state", Icons.Filled.DeleteSweep) { clearDialog = true }
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                SettingsActionRow("Privacy policy", "How MPorTtube handles permissions and data", Icons.Filled.Security, onClick = onOpenPrivacy)
            } }
            item { SettingsSectionTitle("About", Icons.Filled.Info) }
            item { SettingsPanel {
                SettingsActionRow("About MPorTtube", "Version 5.3.4 • App information, features and credits", Icons.Filled.Info, onClick = onOpenAbout)
            } }
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("About MPorTtube") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(R.drawable.app_logo), "MPorTtube logo", Modifier.size(118.dp), contentScale = ContentScale.Fit)
                        Spacer(Modifier.height(14.dp)); Text("MPorTtube", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Premium Media Experience", color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(10.dp)); Text("Version 5.3.4", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            item { AboutCard("What is MPorTtube?", "MPorTtube is a modern media application focused on local media, playback queues, playlists, favorites, history, downloads and a premium Android experience.") }
            item { AboutCard("Core technology", "Kotlin • Jetpack Compose • Material 3 • Media3 / ExoPlayer • Room • Hilt • Android foreground media service.") }
            item { AboutCard("Important notice", "MPorTtube is designed to play media that you own or are authorized to access. Respect copyright, platform rules and applicable laws when using online media sources.") }
            item { Text("© 2026 MPorTtube. All rights reserved.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF93A6BC)) }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Privacy Policy") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text("Privacy Policy", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Effective date: September 10, 2026", style = MaterialTheme.typography.bodySmall, color = Color(0xFF93A6BC)) }
            item { AboutCard("1. Data we handle", "MPorTtube stores app preferences and media-library data on your device. Local playback history, favorites, playlists and download records are used to provide app features.") }
            item { AboutCard("2. Permissions", "Media permissions are requested only for features that browse or play media files. Notification permission is used for download and playback status when supported by Android.") }
            item { AboutCard("3. Network access", "Internet access may be used when you intentionally access network media or download content. MPorTtube should not silently upload your personal media library.") }
            item { AboutCard("4. Local storage", "Preferences are stored locally. Clearing app data from Android settings can remove locally stored preferences and app databases.") }
            item { AboutCard("5. Third-party content", "External media sources may have their own privacy policies and terms. You are responsible for reviewing those policies before using third-party services.") }
            item { AboutCard("6. Your choices", "You can change app preferences in Settings, manage downloads, and clear playback state. Android system settings control app permissions and notification access.") }
            item { AboutCard("7. Changes", "This policy may be updated as the application evolves. The effective date shown on this page identifies the current in-app policy version.") }
            item { Text("For a production release, replace this in-app policy with your final legal/privacy policy and support contact details.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary) }
        }
    }
}

@Composable
private fun AboutCard(title: String, body: String) {
    ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(title, fontWeight = FontWeight.Bold); Spacer(Modifier.height(7.dp)); Text(body, color = Color(0xFFB7C6D8)) } }
}

@Composable
private fun SettingsSectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp, start = 4.dp)) { Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
}

@Composable
private fun SettingsPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp), color = Color(0xFF0D1829), tonalElevation = 2.dp) { Column(content = content) }
}

@Composable
private fun SettingsSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) { Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(12.dp)) }
        Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF93A6BC)) }
        Spacer(Modifier.width(12.dp)); Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsActionRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, showArrow: Boolean = true, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF93A6BC)) }; if (showArrow) Text("›", fontSize = 28.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

