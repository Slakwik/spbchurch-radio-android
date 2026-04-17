package com.spbchurch.radio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spbchurch.radio.ui.theme.Theme

@Composable
fun ArtworkView(
    artworkUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    showTitle: Boolean = false
) {
    val colors = Theme.neumorphic

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(if (size > 100.dp) 16.dp else 8.dp))
            .background(colors.surface),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUrl != null) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(size / 3),
                    tint = colors.accent
                )
                if (showTitle && title.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworkViewFrosted(
    artworkUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val colors = Theme.neumorphic

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUrl != null) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.accent.copy(alpha = 0.3f),
                                colors.background
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(size * 0.85f)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
        ) {
            ArtworkView(
                artworkUrl = artworkUrl,
                title = title,
                modifier = Modifier.fillMaxSize(),
                size = size * 0.85f
            )
        }
    }
}

@Composable
fun DottedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    dotCount: Int = 36,
    dotSize: Dp = 6.dp,
    spacing: Dp = 4.dp,
    activeColor: Color? = null,
    inactiveColor: Color? = null
) {
    val colors = Theme.neumorphic
    val active = activeColor ?: colors.accent
    val inactive = inactiveColor ?: colors.textSecondary.copy(alpha = 0.3f)

    val totalSize = with(androidx.compose.ui.unit.Dp) {
        (dotSize.toPx() + spacing.toPx()) * dotCount
    }

    Canvas(modifier = modifier.size(totalSize.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = (size.width - dotSize.toPx()) / 2
        val angleStep = 360f / dotCount

        for (i in 0 until dotCount) {
            val angle = Math.toRadians((i * angleStep - 90).toDouble())
            val x = centerX + radius * kotlin.math.cos(angle).toFloat()
            val y = centerY + radius * kotlin.math.sin(angle).toFloat()

            val isActive = (i.toFloat() / dotCount) <= progress
            drawCircle(
                color = if (isActive) active else inactive,
                radius = dotSize.toPx() / 2,
                center = Offset(x, y)
            )
        }
    }
}
