package com.spbchurch.radio.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.ui.components.NeumorphicPlayChip
import com.spbchurch.radio.ui.components.TrackListRow
import com.spbchurch.radio.ui.components.TrackRowSubtitle
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun DownloadsScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    // Refresh whenever a download completes (progress map shrinks)
    var downloaded by remember { mutableStateOf(viewModel.getDownloadedTracks()) }
    LaunchedEffect(downloadProgress) {
        downloaded = viewModel.getDownloadedTracks()
    }

    var deleteCandidate by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Загрузки",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        if (downloaded.isEmpty()) {
            EmptyDownloadsState()
        } else {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.SaveAlt,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${downloaded.size} загружено",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(items = downloaded, key = { it.id }) { track ->
                    val isCurrent = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrent && playbackState.isPlaying && !playbackState.isRadioMode

                    TrackListRow(
                        track = track,
                        isCurrentTrack = isCurrent,
                        isPlaying = isPlaying,
                        thumbnailIcon = Icons.Filled.MusicNote,
                        thumbnailTintCurrent = true,
                        subtitle = TrackRowSubtitle(Icons.Filled.SaveAlt, "Сохранено на устройстве"),
                        onPlay = {
                            viewModel.playTrack(track, downloaded)
                            onTrackClick()
                        }
                    ) {
                        IconButton(
                            onClick = { deleteCandidate = track },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Удалить",
                                tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        NeumorphicPlayChip(
                            isCurrentTrack = isCurrent,
                            isPlaying = isPlaying,
                            onClick = {
                                viewModel.playTrack(track, downloaded)
                                onTrackClick()
                            }
                        )
                    }
                }
            }
        }
    }

    deleteCandidate?.let { track ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text("Удалить загрузку?") },
            text = { Text("Трек \"${track.title}\" будет удалён с устройства.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDownload(track)
                    downloaded = viewModel.getDownloadedTracks()
                    deleteCandidate = null
                }) { Text("Удалить", color = colors.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) { Text("Отмена") }
            },
            containerColor = colors.surface
        )
    }
}

@Composable
private fun EmptyDownloadsState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Нет загруженных треков",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Загрузите треки во вкладке \"Треки\"\nдля офлайн-прослушивания",
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
