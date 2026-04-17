package com.spbchurch.radio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MaterialCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    )
}

@Composable
fun MaterialIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor
        )
    ) {
        content()
    }
}

@Composable
fun PlayButtonM3(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val containerColor = if (isPlaying) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = if (isPlaying) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    ) {
        Icon(
            imageVector = if (isPlaying) {
                androidx.compose.material.icons.Icons.Default.Pause
            } else {
                androidx.compose.material.icons.Icons.Default.PlayArrow
            },
            contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
fun TrackRowM3(
    track: com.spbchurch.radio.data.model.Track,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    isFavorite: Boolean,
    downloadState: com.spbchurch.radio.data.model.DownloadState,
    downloadProgress: Float,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    MaterialCard(
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
                    color = if (isCurrentTrack) colors.primary else colors.onSurface,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) {
                        androidx.compose.material.icons.Icons.Default.Favorite
                    } else {
                        androidx.compose.material.icons.Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                    tint = if (isFavorite) colors.primary else colors.onSurfaceVariant
                )
            }

            DownloadButtonM3(
                state = downloadState,
                progress = downloadProgress,
                onDownload = onDownload,
                onCancel = onCancelDownload
            )

            IconButton(onClick = onPlay) {
                Icon(
                    imageVector = if (isCurrentTrack && isPlaying) {
                        androidx.compose.material.icons.Icons.Default.Pause
                    } else {
                        androidx.compose.material.icons.Icons.Default.PlayArrow
                    },
                    contentDescription = "Воспроизвести",
                    tint = if (isCurrentTrack) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DownloadButtonM3(
    state: com.spbchurch.radio.data.model.DownloadState,
    progress: Float,
    onDownload: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    when (state) {
        com.spbchurch.radio.data.model.DownloadState.None -> {
            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Download,
                    contentDescription = "Скачать",
                    tint = colors.onSurfaceVariant
                )
            }
        }
        com.spbchurch.radio.data.model.DownloadState.Downloading -> {
            Box(
                modifier = modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = colors.primary,
                )
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Отменить",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        com.spbchurch.radio.data.model.DownloadState.Downloaded -> {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                contentDescription = "Загружено",
                tint = colors.tertiary,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
            )
        }
        com.spbchurch.radio.data.model.DownloadState.Failed -> {
            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Error,
                    contentDescription = "Ошибка загрузки",
                    tint = colors.error
                )
            }
        }
    }
}

@Composable
fun LiveIndicatorM3(
    isLive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    if (isLive) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colors.error)
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
