package com.spbchurch.radio.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spbchurch.radio.ui.components.MiniPlayerBar
import com.spbchurch.radio.ui.screens.downloads.DownloadsScreen
import com.spbchurch.radio.ui.screens.favorites.FavoritesScreen
import com.spbchurch.radio.ui.screens.player.NowPlayingScreen
import com.spbchurch.radio.ui.screens.radio.RadioScreen
import com.spbchurch.radio.ui.screens.settings.SettingsScreen
import com.spbchurch.radio.ui.screens.tracks.TracksScreen
import com.spbchurch.radio.viewmodel.MainViewModel

private sealed class Tab(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Radio : Tab("radio", "Радио", Icons.Filled.Radio)
    data object Tracks : Tab("tracks", "Треки", Icons.Filled.LibraryMusic)
    data object Favorites : Tab("favorites", "Избранное", Icons.Filled.Favorite)
    data object Downloads : Tab("downloads", "Загрузки", Icons.Filled.Download)
    data object Settings : Tab("settings", "Настройки", Icons.Filled.Settings)

    companion object {
        val all = listOf(Radio, Tracks, Favorites, Downloads, Settings)
    }
}

@Composable
fun SPBChurchRadioApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute != "player"
    // Mini player only when a file is loaded, and never on Radio / Settings / Player itself
    val showMiniPlayer = playbackState.currentTrack != null &&
        !playbackState.isRadioMode &&
        currentRoute != Tab.Radio.route &&
        currentRoute != Tab.Settings.route &&
        currentRoute != "player"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                Column {
                    AnimatedVisibility(
                        visible = showMiniPlayer,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        playbackState.currentTrack?.let { track ->
                            val progress = if (playbackState.duration > 0)
                                playbackState.position.toFloat() / playbackState.duration else 0f
                            MiniPlayerBar(
                                track = track,
                                isPlaying = playbackState.isPlaying,
                                progress = progress,
                                artwork = playbackState.artwork,
                                onPlayPause = { viewModel.togglePlayPause() },
                                onPrevious = { viewModel.previousTrack() },
                                onNext = { viewModel.nextTrack() },
                                onClose = { viewModel.stopFile() },
                                onExpand = { navController.navigate("player") },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    BottomTabBar(
                        currentRoute = currentRoute,
                        onSelect = { tab ->
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Radio.route,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            composable(Tab.Radio.route) {
                RadioScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate("player") },
                    onFindInLibrary = { query ->
                        viewModel.setSearchQuery(query)
                        navController.navigate(Tab.Tracks.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Tab.Tracks.route) {
                TracksScreen(viewModel = viewModel, onTrackClick = { navController.navigate("player") })
            }
            composable(Tab.Favorites.route) {
                FavoritesScreen(viewModel = viewModel, onTrackClick = { navController.navigate("player") })
            }
            composable(Tab.Downloads.route) {
                DownloadsScreen(viewModel = viewModel, onTrackClick = { navController.navigate("player") })
            }
            composable(Tab.Settings.route) {
                SettingsScreen()
            }
            composable("player") {
                NowPlayingScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomTabBar(currentRoute: String?, onSelect: (Tab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    NavigationBar(
        containerColor = colors.background,
        contentColor = colors.onBackground,
        tonalElevation = 0.dp
    ) {
        Tab.all.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(tab) },
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(30.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    unselectedIconColor = colors.onSurfaceVariant,
                    indicatorColor = colors.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
