package com.spbchurch.radio.ui.screens.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.PlaybackOrder
import com.spbchurch.radio.ui.components.ArtworkView
import com.spbchurch.radio.ui.components.MaterialIconButton
import com.spbchurch.radio.ui.components.PlayButtonM3
import com.spbchurch.radio.ui.components.LiveIndicatorM3
import com.spbchurch.radio.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    var showMenu by remember { mutableStateOf(false) }
    var showOrderMenu by remember { mutableStateOf(false) }

    val dragOffset = remember { mutableFloatStateOf(0f) }

    val progress = if (playbackState.isRadioMode) {
        0f
    } else {
        playbackState.progress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
                .blur(30.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.primary.copy(alpha = 0.2f),
                            colors.surface,
                            colors.surface
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragOffset.floatValue > 100) {
                                onBack()
                            }
                            dragOffset.floatValue = 0f
                        },
                        onVerticalDrag = { _, dragAmount ->
                            dragOffset.floatValue += dragAmount
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = if (playbackState.isRadioMode) "Радио" else "Музыка",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Закрыть",
                            tint = colors.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Действия",
                                tint = colors.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            playbackState.currentTrack?.let { track ->
                                val isFavorite = viewModel.isFavorite(track)
                                DropdownMenuItem(
                                    text = {
                                        Text(if (isFavorite) "Убрать из избранного" else "В избранное")
                                    },
                                    onClick = {
                                        viewModel.toggleFavorite(track)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface.copy(alpha = 0f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ArtworkView(
                artworkUrl = null,
                title = playbackState.currentTrack?.title ?: "",
                size = 240.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (playbackState.isRadioMode) {
                LiveIndicatorM3(
                    isLive = playbackState.isPlaying,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = playbackState.currentTitle.ifBlank {
                    playbackState.currentTrack?.title ?: "Выберите трек"
                },
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!playbackState.isRadioMode && playbackState.duration > 0) {
                Slider(
                    value = progress,
                    onValueChange = { newProgress ->
                        viewModel.seekTo((newProgress * playbackState.duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.onSurfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(playbackState.position),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = formatDuration(playbackState.duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showOrderMenu = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    val currentOrder = playbackState.playbackOrder
                    val orderIcon = when (currentOrder) {
                        PlaybackOrder.SHUFFLE -> Icons.Default.Shuffle
                        PlaybackOrder.REPEAT -> Icons.Default.Repeat
                        PlaybackOrder.PLAY_ONCE -> Icons.Default.RepeatOne
                    }

                    Icon(
                        imageVector = orderIcon,
                        contentDescription = "Порядок",
                        tint = if (currentOrder == PlaybackOrder.SHUFFLE)
                            colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOrderMenu,
                    onDismissRequest = { showOrderMenu = false }
                ) {
                    listOf(
                        PlaybackOrder.SHUFFLE to "Микс",
                        PlaybackOrder.REPEAT to "Повтор",
                        PlaybackOrder.PLAY_ONCE to "До конца"
                    ).forEach { (order, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setPlaybackOrder(order)
                                showOrderMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PlayCircle, null)
                            }
                        )
                    }
                }

                MaterialIconButton(
                    onClick = { viewModel.previousTrack() },
                    size = 48.dp,
                    containerColor = colors.surfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Предыдущий",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                PlayButtonM3(
                    isPlaying = playbackState.isPlaying,
                    onClick = { viewModel.togglePlayPause() },
                    size = 80.dp
                )

                MaterialIconButton(
                    onClick = { viewModel.nextTrack() },
                    size = 48.dp,
                    containerColor = colors.surfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Следующий",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                playbackState.currentTrack?.let { track ->
                    IconButton(
                        onClick = { viewModel.toggleFavorite(track) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (viewModel.isFavorite(track))
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (viewModel.isFavorite(track))
                                colors.primary else colors.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
