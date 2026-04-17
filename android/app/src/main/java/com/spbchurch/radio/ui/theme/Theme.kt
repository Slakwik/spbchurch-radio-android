package com.spbchurch.radio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class NeumorphicColors(
    val background: Color,
    val surface: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val success: Color,
    val error: Color
)

private val LightColors = NeumorphicColors(
    background = Color(0xFFF0F3F5),
    surface = Color(0xFFF2F2F5),
    shadowLight = Color(0xFFFFFFFF),
    shadowDark = Color(0xFFA8ABB5),
    textPrimary = Color(0xFF1F1F24),
    textSecondary = Color(0xFF737380),
    accent = Color(0xFFD4A23A),
    success = Color(0xFF33C75A),
    error = Color(0xFFE63835)
)

private val DarkColors = NeumorphicColors(
    background = Color(0xFF1C1C24),
    surface = Color(0xFF26262E),
    shadowLight = Color(0xFFFFFFFF),
    shadowDark = Color(0xFF000000),
    textPrimary = Color(0xFFF2F2F5),
    textSecondary = Color(0xFF9999A6),
    accent = Color(0xFFE8BE5A),
    success = Color(0xFF4DD973),
    error = Color(0xFFF3594C)
)

val LocalNeumorphicColors = compositionLocalOf { LightColors }

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.accent,
    onPrimary = DarkColors.background,
    secondary = DarkColors.textSecondary,
    onSecondary = DarkColors.textPrimary,
    tertiary = DarkColors.success,
    background = DarkColors.background,
    onBackground = DarkColors.textPrimary,
    surface = DarkColors.surface,
    onSurface = DarkColors.textPrimary,
    error = DarkColors.error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightColors.accent,
    onPrimary = LightColors.background,
    secondary = LightColors.textSecondary,
    onSecondary = LightColors.textPrimary,
    tertiary = LightColors.success,
    background = LightColors.background,
    onBackground = LightColors.textPrimary,
    surface = LightColors.surface,
    onSurface = LightColors.textPrimary,
    error = LightColors.error,
    onError = Color.White
)

@Composable
fun SPBChurchRadioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val neumorphic = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = neumorphic.background.toArgb()
            window.navigationBarColor = neumorphic.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalNeumorphicColors provides neumorphic) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

object Theme {
    val neumorphic: NeumorphicColors
        @Composable
        get() = LocalNeumorphicColors.current
}
