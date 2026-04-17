package com.spbchurch.radio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spbchurch.radio.data.model.Track
import com.spbchurch.radio.ui.theme.Theme

@Composable
fun MiniPlayerBar(
    track: Track?,
    isPlaying: Boolean,
    isRadioMode: Boolean,
    currentTitle: String,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.neumorphic

    if (track == null && !isRadioMode) return

    NeumorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onExpand),
        onClick = onExpand
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtworkView(
                artworkUrl = null,
                title = "",
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRadioMode) "Радио" else track?.title ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isRadioMode) currentTitle else "Воспроизведение",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isRadioMode) {
                MiniEqualizerView(
                    isPlaying = isPlaying,
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Следующий",
                        tint = colors.textPrimary
                    )
                }
            }

            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
                    tint = colors.accent
                )
            }
        }
    }
}
