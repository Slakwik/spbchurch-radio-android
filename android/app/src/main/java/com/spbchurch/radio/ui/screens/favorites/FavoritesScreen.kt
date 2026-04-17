package com.spbchurch.radio.ui.screens.favorites

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.ui.components.DownloadStateButton
import com.spbchurch.radio.ui.components.FavoriteHeartButton
import com.spbchurch.radio.ui.components.NeumorphicPlayChip
import com.spbchurch.radio.ui.components.TrackListRow
import com.spbchurch.radio.ui.components.TrackRowSubtitle
import androidx.compose.material.icons.filled.Download
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Избранное",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        if (favorites.isEmpty()) {
            EmptyFavoritesState()
        } else {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${favorites.size} в избранном",
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
                items(items = favorites, key = { it.id }) { track ->
                    val isCurrent = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrent && playbackState.isPlaying && !playbackState.isRadioMode
                    val downloadState = viewModel.getDownloadState(track)
                    val isDownloaded = downloadState == DownloadState.Downloaded

                    TrackListRow(
                        track = track,
                        isCurrentTrack = isCurrent,
                        isPlaying = isPlaying,
                        thumbnailIcon = Icons.Filled.Favorite,
                        thumbnailTintCurrent = true,
                        subtitle = if (isDownloaded) TrackRowSubtitle(
                            Icons.Filled.Download, "Загружено"
                        ) else null,
                        onPlay = {
                            viewModel.playTrack(track, favorites)
                            onTrackClick()
                        }
                    ) {
                        DownloadStateButton(
                            state = downloadState,
                            progress = viewModel.getDownloadProgress(track),
                            onDownload = { viewModel.downloadTrack(track) },
                            onCancel = { viewModel.cancelDownload(track) }
                        )
                        FavoriteHeartButton(
                            isFavorite = true,
                            onClick = { viewModel.toggleFavorite(track) }
                        )
                        NeumorphicPlayChip(
                            isCurrentTrack = isCurrent,
                            isPlaying = isPlaying,
                            onClick = {
                                viewModel.playTrack(track, favorites)
                                onTrackClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState() {
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
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = colors.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Нет избранных треков",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Нажмите ♡ на экране плеера,\nчтобы добавить трек сюда",
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
