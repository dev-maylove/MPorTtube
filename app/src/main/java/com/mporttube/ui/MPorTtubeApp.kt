package com.mporttube.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.dynamicDarkColorScheme
import android.os.Build
import com.mporttube.data.settings.SettingsStore
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
import com.mporttube.ui.screen.HomeScreen
import com.mporttube.ui.screen.MusicScreen
import com.mporttube.ui.screen.PremiumSplashScreen
import com.mporttube.ui.screen.QueueScreen
import com.mporttube.ui.screen.SearchScreen
import com.mporttube.ui.screen.SettingsScreen
import com.mporttube.ui.screen.AboutScreen
import com.mporttube.ui.screen.PrivacyPolicyScreen

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

private val MidnightScheme = darkColorScheme(
    primary = Color(0xFF6FA8FF), secondary = Color(0xFF65D6FF), tertiary = Color(0xFF9B8CFF),
    background = Color(0xFF050B16), surface = Color(0xFF0A1222), surfaceVariant = Color(0xFF121E34),
    onPrimary = Color.White, onBackground = Color(0xFFF4F6FF), onSurface = Color(0xFFF4F6FF)
)
private val OledScheme = darkColorScheme(
    primary = Color(0xFF5AB7FF), secondary = Color(0xFF42E5D1), tertiary = Color(0xFF9D7CFF),
    background = Color.Black, surface = Color(0xFF050505), surfaceVariant = Color(0xFF101010),
    onPrimary = Color.White, onBackground = Color(0xFFF5F5F5), onSurface = Color(0xFFF5F5F5)
)

@Composable
fun MPorTtubeApp() {
    val context = LocalContext.current
    val settings = remember { SettingsStore(context.applicationContext) }
    val dynamicTheme by settings.dynamicTheme.collectAsState()
    val interfaceStyle by settings.interfaceStyle.collectAsState()
    val colorScheme = when {
        dynamicTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicDarkColorScheme(context)
        interfaceStyle == "Midnight blue" -> MidnightScheme
        interfaceStyle == "OLED black" -> OledScheme
        else -> Scheme
    }

    MaterialTheme(colorScheme = colorScheme) {
        var splash by rememberSaveable { mutableStateOf(true) }

        if (splash) {
            PremiumSplashScreen { splash = false }
            return@MaterialTheme
        }

        val nav = rememberNavController()

        NavHost(navController = nav, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    openPlayer = { nav.navigate("player") },
                    openHistory = { nav.navigate("history") },
                    openFavorites = { nav.navigate("favorites") },
                    openPlaylists = { nav.navigate("playlists") },
                    openDownloads = { nav.navigate("downloads") },
                    openMusic = { nav.navigate("music") }
                )
            }

            composable("music") {
                MusicScreen(
                    onBack = { nav.popBackStack() },
                    onOpenPlayer = { nav.navigate("player") }
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
                SettingsScreen(
                    onBack = { nav.popBackStack() },
                    onOpenAbout = { nav.navigate("about") },
                    onOpenPrivacy = { nav.navigate("privacy") },
                    onOpenDownloads = { nav.navigate("downloads") }
                )
            }

            composable("about") {
                AboutScreen(onBack = { nav.popBackStack() })
            }

            composable("privacy") {
                PrivacyPolicyScreen(onBack = { nav.popBackStack() })
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
