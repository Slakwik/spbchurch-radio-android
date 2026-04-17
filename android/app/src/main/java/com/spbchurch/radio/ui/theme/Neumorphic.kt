package com.spbchurch.radio.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Approximates the iOS neumorphic look with paired light + dark shadows.
 * Compose has no native double-shadow API, so we draw two large translucent
 * rounded rects behind the content offset in opposite directions.
 */
fun Modifier.neumorphicRaised(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    blurRadius: Dp = 8.dp,
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val shadowDark = if (isDark) AppColors.DarkShadowDark else AppColors.LightShadowDark
    val shadowLight = if (isDark) AppColors.DarkShadowLight else AppColors.LightShadowLight
    neumorphicShadow(
        cornerRadius = cornerRadius,
        elevation = elevation,
        blurRadius = blurRadius,
        darkShadow = shadowDark,
        lightShadow = shadowLight
    )
}

fun Modifier.neumorphicPressed(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 2.dp,
    blurRadius: Dp = 3.dp,
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val shadowDark = (if (isDark) AppColors.DarkShadowDark else AppColors.LightShadowDark)
        .copy(alpha = 0.3f)
    val shadowLight = (if (isDark) AppColors.DarkShadowLight else AppColors.LightShadowLight)
        .copy(alpha = 0.5f)
    neumorphicShadow(
        cornerRadius = cornerRadius,
        elevation = elevation,
        blurRadius = blurRadius,
        darkShadow = shadowDark,
        lightShadow = shadowLight
    )
}

private fun Modifier.neumorphicShadow(
    cornerRadius: Dp,
    elevation: Dp,
    blurRadius: Dp,
    darkShadow: Color,
    lightShadow: Color,
): Modifier = drawBehind {
    val cr = cornerRadius.toPx()
    val off = elevation.toPx()
    val blur = blurRadius.toPx()

    drawNeumorphicShadow(
        color = darkShadow,
        offset = Offset(off, off),
        blur = blur,
        cornerRadius = cr
    )
    drawNeumorphicShadow(
        color = lightShadow,
        offset = Offset(-off, -off),
        blur = blur,
        cornerRadius = cr
    )
}

/** Layered translucent shadows simulate Gaussian blur — cheap and good enough. */
private fun DrawScope.drawNeumorphicShadow(
    color: Color,
    offset: Offset,
    blur: Float,
    cornerRadius: Float
) {
    val layers = 6
    val perStep = blur / layers
    for (i in 1..layers) {
        val expand = perStep * i
        val alpha = color.alpha * (1f - i.toFloat() / (layers + 1))
        val layerColor = color.copy(alpha = alpha)
        drawRoundRectOffset(
            color = layerColor,
            topLeft = Offset(offset.x - expand / 2, offset.y - expand / 2),
            sizeWidth = size.width + expand,
            sizeHeight = size.height + expand,
            cornerRadius = cornerRadius + expand / 2
        )
    }
}

private fun DrawScope.drawRoundRectOffset(
    color: Color,
    topLeft: Offset,
    sizeWidth: Float,
    sizeHeight: Float,
    cornerRadius: Float
) {
    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = androidx.compose.ui.geometry.Size(sizeWidth, sizeHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
    )
}
