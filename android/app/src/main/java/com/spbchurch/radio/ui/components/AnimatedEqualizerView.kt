package com.spbchurch.radio.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Audio equalizer bars — heights animate randomly while [isPlaying] is true,
 * collapse to [minHeight] otherwise. Mirrors the iOS AnimatedEqualizerView.
 */
@Composable
fun AnimatedEqualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barWidth: Dp = 4.dp,
    barSpacing: Dp = 3.dp,
    minHeight: Dp = 4.dp,
    maxHeight: Dp = 28.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 2.dp
) {
    var heights by remember { mutableStateOf(List(barCount) { minHeight }) }

    LaunchedEffect(isPlaying, barCount) {
        if (!isPlaying) {
            heights = List(barCount) { minHeight }
            return@LaunchedEffect
        }
        while (true) {
            heights = List(barCount) {
                Dp(Random.nextFloat() * (maxHeight.value - minHeight.value) + minHeight.value)
            }
            delay(150)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { target ->
            val animated by animateDpAsState(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "bar"
            )
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(animated)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(color)
            )
        }
    }
}

@Composable
fun MiniEqualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 14.dp
) {
    AnimatedEqualizerView(
        isPlaying = isPlaying,
        modifier = modifier,
        barCount = 3,
        barWidth = 2.5.dp,
        barSpacing = 2.dp,
        minHeight = 3.dp,
        maxHeight = maxHeight,
        cornerRadius = 1.5.dp
    )
}

@Composable
fun LargeEqualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedEqualizerView(
        isPlaying = isPlaying,
        modifier = modifier,
        barCount = 7,
        barWidth = 5.dp,
        barSpacing = 3.dp,
        minHeight = 6.dp,
        maxHeight = 36.dp,
        cornerRadius = 2.5.dp
    )
}
