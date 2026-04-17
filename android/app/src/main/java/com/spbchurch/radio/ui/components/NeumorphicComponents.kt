package com.spbchurch.radio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spbchurch.radio.ui.theme.Theme

@Composable
fun NeumorphicSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
    pressed: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = Theme.neumorphic
    val backgroundColor = colors.background

    val shadowLight = if (pressed) colors.shadowDark.copy(alpha = 0.3f) else colors.shadowLight.copy(alpha = 0.7f)
    val shadowDark = if (pressed) colors.shadowLight.copy(alpha = 0.06f) else colors.shadowDark.copy(alpha = 0.5f)

    val shadowElevation = if (pressed) 2.dp else elevation

    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = shadowDark,
                spotColor = shadowDark
            )
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = shadowLight,
                spotColor = shadowLight
            )
            .background(backgroundColor, shape)
            .then(
                if (pressed) {
                    Modifier
                        .background(
                            colors.surface,
                            shape
                        )
                        .padding(1.dp)
                } else Modifier
            ),
        content = content
    )
}

@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = Theme.neumorphic

    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = colors.shadowDark.copy(alpha = 0.5f),
                spotColor = colors.shadowDark.copy(alpha = 0.5f)
            )
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = colors.shadowLight.copy(alpha = 0.7f),
                spotColor = colors.shadowLight.copy(alpha = 0.7f)
            )
            .background(colors.background, shape)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        content = content
    )
}

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = Theme.neumorphic

    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = colors.shadowDark.copy(alpha = 0.5f),
                spotColor = colors.shadowDark.copy(alpha = 0.5f)
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = colors.shadowLight.copy(alpha = 0.7f),
                spotColor = colors.shadowLight.copy(alpha = 0.7f)
            )
            .background(colors.background, RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun NeumorphicIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    icon: @Composable () -> Unit
) {
    NeumorphicButton(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}
