package com.spbchurch.radio.ui.screens.downloads

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
import com.spbchurch.radio.ui.components.ArtworkView
import com.spbchurch.radio.ui.components.MaterialCard
import com.spbchurch.radio.ui.components.MaterialIconButton
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun DownloadsScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val downloadedTracks = remember { mutableStateOf<List<Track>>(emptyList()) }
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    var showDeleteDialog by remember { mutableStateOf<Track?>(null) }

    LaunchedEffect(Unit) {
        downloadedTracks.value = viewModel.getDownloadedTracks()
    }

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
                text = stringResource(R.string.downloads),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onSurface
            )

            if (downloadedTracks.value.isNotEmpty()) {
                Text(
                    text = "${downloadedTracks.value.size} треков",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }

        if (downloadedTracks.value.isEmpty()) {
            EmptyStateView(
                icon = {
                    Icon(
                        Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colors.onSurfaceVariant
                    )
                },
                title = stringResource(R.string.no_downloads),
                subtitle = "Загружайте треки для прослушивания без интернета",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = downloadedTracks.value,
                    key = { it.id }
                ) { track ->
                    val isCurrentTrack = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrentTrack && playbackState.isPlaying && !playbackState.isRadioMode

                    DownloadedTrackRow(
                        track = track,
                        isPlaying = isPlaying,
                        isCurrentTrack = isCurrentTrack,
                        isFavorite = viewModel.isFavorite(track),
                        onPlay = {
                            viewModel.playTrack(track, downloadedTracks.value)
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
            title = { Text("Удалить загрузку?") },
            text = { Text("Трек \"${track.title}\" будет удалён с устройства.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDownload(track)
                        downloadedTracks.value = viewModel.getDownloadedTracks()
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
            }
        )
    }
}

@Composable
private fun DownloadedTrackRow(
    track: Track,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    MaterialCard(
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
                    color = if (isCurrentTrack) colors.primary else colors.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = colors.tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Загружено",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                    tint = if (isFavorite) colors.primary else colors.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = colors.error
                )
            }

            MaterialIconButton(
                onClick = onPlay,
                size = 44.dp,
                containerColor = colors.primaryContainer
            ) {
                Icon(
                    imageVector = if (isCurrentTrack && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Воспроизвести",
                    tint = if (isCurrentTrack) colors.onPrimaryContainer else colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
