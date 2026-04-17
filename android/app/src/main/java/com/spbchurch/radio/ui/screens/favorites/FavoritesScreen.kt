package com.spbchurch.radio.ui.screens.favorites

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

                    FavoriteTrackRow(
                        track = track,
                        isPlaying = isPlaying,
                        isCurrentTrack = isCurrentTrack,
                        onPlay = {
                            viewModel.playTrack(track, favorites)
                            onTrackClick()
                        },
                        onFavorite = { viewModel.toggleFavorite(track) },
                        onDelete = { showDeleteDialog = track }
                    )
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

@Composable
private fun FavoriteTrackRow(
    track: Track,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = Theme.neumorphic

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPlay
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtworkView(
                artworkUrl = null,
                title = track.title,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentTrack) colors.accent else colors.textPrimary
                )
            }

            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Убрать из избранного",
                    tint = colors.accent
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = colors.error
                )
            }

            NeumorphicIconButton(
                onClick = onPlay,
                size = 44.dp
            ) {
                Icon(
                    imageVector = if (isCurrentTrack && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Воспроизвести",
                    tint = if (isCurrentTrack) colors.accent else colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
