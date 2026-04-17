package com.spbchurch.radio.ui.screens.favorites

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.R
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.ui.components.*
import com.spbchurch.radio.ui.theme.Theme
import com.spbchurch.radio.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = Theme.neumorphic

    var showDeleteDialog by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )

            if (favorites.isNotEmpty()) {
                Text(
                    text = "${favorites.size} треков",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary
                )
            }
        }

        if (favorites.isEmpty()) {
            EmptyStateView(
                icon = {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colors.textSecondary
                    )
                },
                title = stringResource(R.string.no_favorites),
                subtitle = "Добавляйте треки в избранное, нажимая на сердечко",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { track ->
                    val isCurrentTrack = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrentTrack && playbackState.isPlaying && !playbackState.isRadioMode

                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    showDeleteDialog = track
                                }
                                false
                            }
                        ),
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colors.error.copy(alpha = 0.2f))
                                    .padding(end = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = colors.error
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false
                    ) {
                        TrackRow(
                            track = track,
                            isPlaying = isPlaying,
                            isCurrentTrack = isCurrentTrack,
                            isFavorite = true,
                            downloadState = viewModel.getDownloadState(track),
                            downloadProgress = viewModel.getDownloadProgress(track),
                            onPlay = {
                                viewModel.playTrack(track, favorites)
                                onTrackClick()
                            },
                            onFavorite = { viewModel.toggleFavorite(track) },
                            onDownload = { viewModel.downloadTrack(track) },
                            onCancelDownload = { viewModel.cancelDownload(track) }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { track ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить из избранного?") },
            text = { Text("Трек \"${track.title}\" будет удалён из избранного.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.toggleFavorite(track)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Удалить", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Отмена")
                }
            },
            containerColor = colors.surface
        )
    }
}
