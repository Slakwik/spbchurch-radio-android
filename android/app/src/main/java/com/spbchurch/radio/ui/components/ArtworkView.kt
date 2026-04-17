package com.spbchurch.radio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Square neumorphic artwork tile. Falls back to a music note icon when [artwork]
 * is null. Pass any model Coil understands (URL, ByteArray, Bitmap, etc.).
 */
@Composable
fun ArtworkView(
    artwork: Any?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    cornerRadius: Dp = 12.dp
) {
    val colors = MaterialTheme.colorScheme
    val shape: Shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(colors.surface),
        contentAlignment = Alignment.Center
    ) {
        if (artwork != null) {
            AsyncImage(
                model = artwork,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(size * 0.35f),
                tint = colors.primary.copy(alpha = 0.3f)
            )
        }
    }
}

/** Circular artwork used in the Now Playing screen behind the dotted ring. */
@Composable
fun ArtworkViewFrosted(
    artwork: Any?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        colors.surface,
                        colors.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (artwork != null) {
            AsyncImage(
                model = artwork,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(size * 0.25f),
                tint = colors.primary.copy(alpha = 0.4f)
            )
        }
    }
}
