package com.spbchurch.radio.ui.screens.tracks

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.SortOrder
import com.spbchurch.radio.ui.components.DownloadStateButton
import com.spbchurch.radio.ui.components.FavoriteHeartButton
import com.spbchurch.radio.ui.components.NeumorphicPlayChip
import com.spbchurch.radio.ui.components.TrackListRow
import com.spbchurch.radio.ui.components.TrackRowSubtitle
import androidx.compose.material.icons.filled.CheckCircle
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun TracksScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val tracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    var sortMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Треки",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { sortMenuOpen = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Сортировка",
                        tint = colors.primary
                    )
                }
                DropdownMenu(
                    expanded = sortMenuOpen,
                    onDismissRequest = { sortMenuOpen = false }
                ) {
                    listOf(
                        SortOrder.DEFAULT to "По умолчанию",
                        SortOrder.A_TO_Z to "По названию (А–Я)",
                        SortOrder.Z_TO_A to "По названию (Я–А)"
                    ).forEach { (order, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            leadingIcon = {
                                if (sortOrder == order) {
                                    Icon(Icons.Filled.Check, null, tint = colors.primary)
                                }
                            },
                            onClick = {
                                viewModel.setSortOrder(order)
                                sortMenuOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск по названию...", color = colors.onSurfaceVariant) },
            leadingIcon = {
                Icon(Icons.Filled.Search, null, tint = colors.onSurfaceVariant)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Filled.Clear, "Очистить", tint = colors.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline
            )
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading && tracks.isEmpty() -> LoadingState()
            error != null && tracks.isEmpty() -> ErrorState(
                message = error ?: "",
                onRetry = { viewModel.loadTracks() }
            )
            tracks.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "Ничего не найдено" else "Треки не найдены",
                    color = colors.onSurfaceVariant
                )
            }
            else -> {
                Text(
                    text = "${tracks.size} треков",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(items = tracks, key = { it.id }) { track ->
                        val isCurrent = playbackState.currentTrack?.id == track.id
                        val isPlaying = isCurrent && playbackState.isPlaying && !playbackState.isRadioMode
                        val isDownloaded = viewModel.getDownloadState(track) ==
                                com.spbchurch.radio.data.model.DownloadState.Downloaded

                        TrackListRow(
                            track = track,
                            isCurrentTrack = isCurrent,
                            isPlaying = isPlaying,
                            subtitle = if (isDownloaded) TrackRowSubtitle(
                                Icons.Filled.CheckCircle,
                                "Загружено"
                            ) else null,
                            onPlay = {
                                viewModel.playTrack(track)
                                onTrackClick()
                            }
                        ) {
                            FavoriteHeartButton(
                                isFavorite = favorites.any { it.url == track.url },
                                onClick = { viewModel.toggleFavorite(track) }
                            )
                            DownloadStateButton(
                                state = viewModel.getDownloadState(track),
                                progress = viewModel.getDownloadProgress(track),
                                onDownload = { viewModel.downloadTrack(track) },
                                onCancel = { viewModel.cancelDownload(track) }
                            )
                            NeumorphicPlayChip(
                                isCurrentTrack = isCurrent,
                                isPlaying = isPlaying,
                                onClick = {
                                    viewModel.playTrack(track)
                                    onTrackClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { i -> PulsingDot(delay = i * 200) }
        }
        Spacer(Modifier.height(16.dp))
        Text("Загрузка треков...", color = colors.onSurfaceVariant, fontSize = 14.sp)
    }
}

@Composable
private fun PulsingDot(delay: Int) {
    val colors = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "pulsing_dot")
    val scale by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(colors.primary)
    )
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
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
                imageVector = Icons.Filled.WifiOff,
                contentDescription = null,
                tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(message, color = colors.onSurfaceVariant, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.primary, CircleShape)
                .clickable(onClick = onRetry)
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                "Повторить",
                color = colors.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
