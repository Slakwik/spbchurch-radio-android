package com.spbchurch.radio.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spbchurch.radio.R
import com.spbchurch.radio.ui.components.MiniPlayerBar
import com.spbchurch.radio.ui.screens.downloads.DownloadsScreen
import com.spbchurch.radio.ui.screens.favorites.FavoritesScreen
import com.spbchurch.radio.ui.screens.player.NowPlayingScreen
import com.spbchurch.radio.ui.screens.radio.RadioScreen
import com.spbchurch.radio.ui.screens.settings.SettingsScreen
import com.spbchurch.radio.ui.screens.tracks.TracksScreen
import com.spbchurch.radio.ui.theme.Theme
import com.spbchurch.radio.viewmodel.MainViewModel

sealed class Screen(
    val route: String,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Radio : Screen("radio", R.string.radio, Icons.Filled.Radio, Icons.Outlined.Radio)
    data object Tracks : Screen("tracks", R.string.tracks, Icons.Filled.MusicNote, Icons.Outlined.MusicNote)
    data object Favorites : Screen("favorites", R.string.favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    data object Downloads : Screen("downloads", R.string.downloads, Icons.Filled.Download, Icons.Outlined.Download)
    data object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SPBChurchRadioApp(
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Radio,
        Screen.Tracks,
        Screen.Favorites,
        Screen.Downloads,
        Screen.Settings
    )

    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val colors = Theme.neumorphic

    val showBottomBar = currentDestination?.route != "player"
    val showMiniPlayer = playbackState.currentTrack != null || playbackState.isRadioMode

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            if (showBottomBar) {
                Column {
                    AnimatedVisibility(
                        visible = showMiniPlayer,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        MiniPlayerBar(
                            track = playbackState.currentTrack,
                            isPlaying = playbackState.isPlaying,
                            isRadioMode = playbackState.isRadioMode,
                            currentTitle = playbackState.currentTitle,
                            onPlayPause = { viewModel.togglePlayPause() },
                            onNext = { viewModel.nextTrack() },
                            onExpand = { navController.navigate("player") },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    NavigationBar(
                        containerColor = colors.background,
                        contentColor = colors.textPrimary,
                        tonalElevation = 0.dp
                    ) {
                        screens.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = stringResource(screen.titleRes)
                                    )
                                },
                                label = { Text(stringResource(screen.titleRes)) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colors.accent,
                                    selectedTextColor = colors.accent,
                                    unselectedIconColor = colors.textSecondary,
                                    unselectedTextColor = colors.textSecondary,
                                    indicatorColor = colors.surface
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Radio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Radio.route) {
                RadioScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate("player") }
                )
            }
            composable(Screen.Tracks.route) {
                TracksScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate("player") }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate("player") }
                )
            }
            composable(Screen.Downloads.route) {
                DownloadsScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate("player") }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable("player") {
                NowPlayingScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
