package com.spbchurch.radio.ui.screens.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.PlaybackOrder
import com.spbchurch.radio.ui.components.*
import com.spbchurch.radio.ui.theme.Theme
import com.spbchurch.radio.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = Theme.neumorphic

    var showMenu by remember { mutableStateOf(false) }
    var showOrderMenu by remember { mutableStateOf(false) }

    val dragOffset = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    val progress = if (playbackState.isRadioMode) {
        0f
    } else {
        playbackState.progress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
                .blur(30.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.accent.copy(alpha = 0.2f),
                            colors.background,
                            colors.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Закрыть",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (playbackState.isRadioMode) "Радио" else "Музыка",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Действия",
                            tint = colors.textPrimary
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

                            val downloadState = viewModel.getDownloadState(track)
                            if (downloadState == DownloadState.None) {
                                DropdownMenuItem(
                                    text = { Text("Скачать") },
                                    onClick = {
                                        viewModel.downloadTrack(track)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Download, null)
                                    }
                                )
                            } else if (downloadState == DownloadState.Downloaded) {
                                DropdownMenuItem(
                                    text = { Text("Удалить загрузку") },
                                    onClick = {
                                        viewModel.deleteDownload(track)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, null)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            ArtworkView(
                artworkUrl = null,
                title = playbackState.currentTrack?.title ?: "",
                size = 280.dp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (playbackState.isRadioMode) {
                LiveIndicator(
                    isLive = playbackState.isPlaying,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = playbackState.currentTitle.ifBlank {
                    playbackState.currentTrack?.title ?: "Выберите трек"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!playbackState.isRadioMode && playbackState.duration > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = progress,
                    onValueChange = { newProgress ->
                        viewModel.seekTo((newProgress * playbackState.duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = colors.accent,
                        activeTrackColor = colors.accent,
                        inactiveTrackColor = colors.textSecondary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(playbackState.position),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = formatDuration(playbackState.duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    val currentOrder = playbackState.playbackOrder
                    val orderIcon = when (currentOrder) {
                        PlaybackOrder.SHUFFLE -> Icons.Default.Shuffle
                        PlaybackOrder.REPEAT -> Icons.Default.Repeat
                        PlaybackOrder.PLAY_ONCE -> Icons.Default.Repeat
                    }

                    IconButton(onClick = { showOrderMenu = true }) {
                        Icon(
                            imageVector = orderIcon,
                            contentDescription = "Порядок воспроизведения",
                            tint = if (currentOrder == PlaybackOrder.SHUFFLE)
                                colors.accent else colors.textSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = showOrderMenu,
                        onDismissRequest = { showOrderMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Микс") },
                            onClick = {
                                viewModel.setPlaybackOrder(PlaybackOrder.SHUFFLE)
                                showOrderMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Shuffle, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Повтор") },
                            onClick = {
                                viewModel.setPlaybackOrder(PlaybackOrder.REPEAT)
                                showOrderMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Repeat, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("До конца") },
                            onClick = {
                                viewModel.setPlaybackOrder(PlaybackOrder.PLAY_ONCE)
                                showOrderMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Repeat, null)
                            }
                        )
                    }
                }

                NeumorphicIconButton(
                    onClick = { viewModel.previousTrack() },
                    size = 56.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Предыдущий",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                PlayButton(
                    isPlaying = playbackState.isPlaying,
                    onClick = { viewModel.togglePlayPause() },
                    size = 96.dp
                )

                NeumorphicIconButton(
                    onClick = { viewModel.nextTrack() },
                    size = 56.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Следующий",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                playbackState.currentTrack?.let { track ->
                    IconButton(onClick = { viewModel.toggleFavorite(track) }) {
                        Icon(
                            imageVector = if (viewModel.isFavorite(track))
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (viewModel.isFavorite(track))
                                colors.accent else colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
