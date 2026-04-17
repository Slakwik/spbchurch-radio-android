package com.spbchurch.radio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.ui.theme.neumorphicRaised

/**
 * Generic neumorphic list row used by Tracks / Favorites / Downloads.
 * Trailing controls and the thumbnail icon are caller-provided so each
 * screen can vary its layout while keeping the same shape.
 */
@Composable
fun TrackListRow(
    track: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    subtitle: TrackRowSubtitle? = null,
    thumbnailIcon: ImageVector = Icons.Filled.MusicNote,
    thumbnailTintCurrent: Boolean = false,
    onPlay: () -> Unit,
    trailing: @Composable Row.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val highlight = if (isCurrentTrack) {
        colors.primary.copy(alpha = if (colors.background.luminanceIsDark()) 0.12f else 0.08f)
    } else colors.background.copy(alpha = 0f)

    val rowBg by animateColorAsState(targetValue = highlight, label = "row_bg")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg, RoundedCornerShape(12.dp))
            .clickable(onClick = onPlay)
            .padding(
                horizontal = if (isCurrentTrack) 10.dp else 0.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Neumorphic thumbnail
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .neumorphicRaised(cornerRadius = 10.dp, elevation = 2.dp, blurRadius = 4.dp)
                .background(colors.background, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCurrentTrack && isPlaying -> MiniEqualizerView(isPlaying = true)
                isCurrentTrack -> Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(14.dp)
                )
                else -> Icon(
                    imageVector = thumbnailIcon,
                    contentDescription = null,
                    tint = if (thumbnailTintCurrent) colors.primary.copy(alpha = 0.5f)
                    else colors.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = track.title,
                fontSize = 15.sp,
                fontWeight = if (isCurrentTrack) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrentTrack) colors.primary else colors.onBackground.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = subtitle.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = subtitle.text,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            content = { trailing() }
        )
    }
}

data class TrackRowSubtitle(val icon: ImageVector, val text: String)

@Composable
fun FavoriteHeartButton(isFavorite: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
            tint = if (isFavorite) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
fun DownloadStateButton(
    state: DownloadState,
    progress: Float,
    onDownload: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val iconSize = 22.dp

    when (state) {
        DownloadState.None -> {
            IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Скачать",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        DownloadState.Downloading -> {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clickable(onClick = onCancel),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
                    color = colors.primary,
                    trackColor = colors.onSurfaceVariant.copy(alpha = 0.15f)
                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Отменить",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        DownloadState.Downloaded -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Загружено",
                tint = colors.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(iconSize)
            )
        }
        DownloadState.Failed -> {
            IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Ошибка загрузки",
                    tint = colors.error,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun NeumorphicPlayChip(
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .neumorphicRaised(cornerRadius = 16.dp, elevation = 2.dp, blurRadius = 3.dp)
            .background(colors.background, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isCurrentTrack && isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = "Воспроизвести",
            tint = if (isCurrentTrack) colors.primary else colors.onBackground,
            modifier = Modifier.size(12.dp)
        )
    }
}

private fun androidx.compose.ui.graphics.Color.luminanceIsDark(): Boolean {
    val r = red
    val g = green
    val b = blue
    val luminance = 0.299f * r + 0.587f * g + 0.114f * b
    return luminance < 0.5f
}
