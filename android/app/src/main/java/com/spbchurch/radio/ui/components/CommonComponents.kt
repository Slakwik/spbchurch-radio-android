package com.spbchurch.radio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.ui.theme.Theme

@Composable
fun PlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val colors = Theme.neumorphic
    val infiniteTransition = rememberInfiniteTransition(label = "play_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .size(size * pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.accent.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        NeumorphicButton(
            onClick = onClick,
            modifier = Modifier.size(size),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Остановить" else "Воспроизвести",
                    modifier = Modifier.size(size / 2),
                    tint = colors.accent
                )
            }
        }
    }
}

@Composable
fun TrackRow(
    track: Track,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    isFavorite: Boolean,
    downloadState: DownloadState,
    downloadProgress: Float,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.neumorphic

    NeumorphicCard(
        modifier = modifier.fillMaxWidth(),
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
                    color = if (isCurrentTrack) colors.accent else colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                    tint = if (isFavorite) colors.accent else colors.textSecondary
                )
            }

            DownloadButton(
                state = downloadState,
                progress = downloadProgress,
                onDownload = onDownload,
                onCancel = onCancelDownload
            )

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

@Composable
fun DownloadButton(
    state: DownloadState,
    progress: Float,
    onDownload: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.neumorphic

    when (state) {
        DownloadState.None -> {
            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Скачать",
                    tint = colors.textSecondary
                )
            }
        }
        DownloadState.Downloading -> {
            Box(
                modifier = modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = colors.accent,
                )
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Отменить",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        DownloadState.Downloaded -> {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Загружено",
                tint = colors.success,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
            )
        }
        DownloadState.Failed -> {
            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Ошибка загрузки",
                    tint = colors.error
                )
            }
        }
    }
}

@Composable
fun LiveIndicator(
    isLive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = Theme.neumorphic

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLive) {
            val infiniteTransition = rememberInfiniteTransition(label = "live")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "live_alpha"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colors.error.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ЭФИР",
                style = MaterialTheme.typography.labelSmall,
                color = colors.error
            )
        }
    }
}

@Composable
fun EmptyStateView(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.neumorphic

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
    }
}
