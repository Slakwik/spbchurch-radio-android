package com.spbchurch.radio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spbchurch.radio.ui.theme.Theme
import kotlin.random.Random

@Composable
fun AnimatedEqualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barWidth: Dp = 3.dp,
    barSpacing: Dp = 2.dp,
    minHeight: Dp = 4.dp,
    maxHeight: Dp = 20.dp
) {
    val colors = Theme.neumorphic
    val accentColor = colors.accent

    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    val animations = (0 until barCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = minHeight.value,
            targetValue = maxHeight.value,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + Random.nextInt(200),
                    delayMillis = index * 50,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = androidx.compose.ui.Alignment.Bottom
    ) {
        animations.forEach { animation ->
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .height(animation.value.dp)
                        .padding(vertical = 1.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = accentColor,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth.toPx() / 2)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .height(minHeight)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = accentColor.copy(alpha = 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth.toPx() / 2)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniEqualizerView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedEqualizerView(
        isPlaying = isPlaying,
        modifier = modifier,
        barCount = 3,
        barWidth = 2.dp,
        barSpacing = 1.dp,
        minHeight = 2.dp,
        maxHeight = 10.dp
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
        barWidth = 4.dp,
        barSpacing = 3.dp,
        minHeight = 6.dp,
        maxHeight = 28.dp
    )
}
