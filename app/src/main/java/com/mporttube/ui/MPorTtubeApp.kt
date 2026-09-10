package com.mporttube.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mporttube.ui.screen.DownloadsScreen
import com.mporttube.ui.screen.FavoritesScreen
import com.mporttube.ui.screen.HistoryScreen
import com.mporttube.ui.screen.LibraryHubScreen
import com.mporttube.ui.screen.PlayerScreen
import com.mporttube.ui.screen.PlaylistDetailScreen
import com.mporttube.ui.screen.PlaylistsScreen
import com.mporttube.ui.screen.PremiumHomeScreen
import com.mporttube.ui.screen.PremiumSplashScreen
import com.mporttube.ui.screen.QueueScreen
import com.mporttube.ui.screen.SearchScreen
import com.mporttube.ui.screen.SettingsScreen

private val Scheme = darkColorScheme(
    primary = Color(0xFF8B7CFF),
    secondary = Color(0xFF4EA7FF),
    tertiary = Color(0xFFE76CFF),
    background = Color(0xFF07101D),
    surface = Color(0xFF0D1726),
    surfaceVariant = Color(0xFF16243A),
    onPrimary = Color.White,
    onBackground = Color(0xFFF4F6FF),
    onSurface = Color(0xFFF4F6FF)
)

@Composable
fun MPorTtubeApp() {
    MaterialTheme(colorScheme = Scheme) {
        var splash by rememberSaveable { mutableStateOf(true) }

        if (splash) {
            PremiumSplashScreen { splash = false }
            return@MaterialTheme
        }

        val nav = rememberNavController()

        NavHost(navController = nav, startDestination = "home") {
            composable("home") {
                PremiumHomeScreen(
                    onOpenPlayer = { nav.navigate("player") },
                    onSearch = { nav.navigate("search") },
                    onQueue = { nav.navigate("queue") },
                    onLibrary = { nav.navigate("library") },
                    onSettings = { nav.navigate("settings") }
                )
            }

            composable("player") {
                PlayerScreen(onBack = { nav.popBackStack() })
            }

            composable("search") {
                SearchScreen(
                    onBack = { nav.popBackStack() },
                    onOpenPlayer = { nav.navigate("player") }
                )
            }

            composable("queue") {
                QueueScreen(
                    onBack = { nav.popBackStack() },
                    onOpenPlayer = { nav.navigate("player") }
                )
            }

            composable("library") {
                LibraryHubScreen(
                    onBack = { nav.popBackStack() },
                    openHistory = { nav.navigate("history") },
                    openFavorites = { nav.navigate("favorites") },
                    openPlaylists = { nav.navigate("playlists") },
                    openDownloads = { nav.navigate("downloads") }
                )
            }

            composable("settings") {
                SettingsScreen(onBack = { nav.popBackStack() })
            }

            composable("history") {
                HistoryScreen(
                    onBack = { nav.popBackStack() },
                    openPlayer = { nav.navigate("player") }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onBack = { nav.popBackStack() },
                    openPlayer = { nav.navigate("player") }
                )
            }

            composable("playlists") {
                PlaylistsScreen(
                    onBack = { nav.popBackStack() },
                    openPlaylist = { id -> nav.navigate("playlist/$id") }
                )
            }

            composable("playlist/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                PlaylistDetailScreen(
                    playlistId = id,
                    onBack = { nav.popBackStack() },
                    openPlayer = { nav.navigate("player") }
                )
            }

            composable("downloads") {
                DownloadsScreen(onBack = { nav.popBackStack() })
            }
        }
    }
}
