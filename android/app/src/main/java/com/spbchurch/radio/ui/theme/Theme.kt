package com.spbchurch.radio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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

val LocalNeumorphicColors = compositionLocalOf {
    NeumorphicColors(
        background = NeumorphicLight.Background,
        surface = NeumorphicLight.Surface,
        shadowLight = NeumorphicLight.ShadowLight,
        shadowDark = NeumorphicLight.ShadowDark,
        textPrimary = NeumorphicLight.TextPrimary,
        textSecondary = NeumorphicLight.TextSecondary,
        accent = NeumorphicLight.Accent,
        success = NeumorphicLight.Success,
        error = NeumorphicLight.Error
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = NeumorphicDark.Accent,
    onPrimary = NeumorphicDark.Background,
    secondary = NeumorphicDark.TextSecondary,
    onSecondary = NeumorphicDark.TextPrimary,
    tertiary = NeumorphicDark.Success,
    background = NeumorphicDark.Background,
    onBackground = NeumorphicDark.TextPrimary,
    surface = NeumorphicDark.Surface,
    onSurface = NeumorphicDark.TextPrimary,
    error = NeumorphicDark.Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NeumorphicLight.Accent,
    onPrimary = NeumorphicLight.Background,
    secondary = NeumorphicLight.TextSecondary,
    onSecondary = NeumorphicLight.TextPrimary,
    tertiary = NeumorphicLight.Success,
    background = NeumorphicLight.Background,
    onBackground = NeumorphicLight.TextPrimary,
    surface = NeumorphicLight.Surface,
    onSurface = NeumorphicLight.TextPrimary,
    error = NeumorphicLight.Error,
    onError = Color.White
)

@Composable
fun SPBChurchRadioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val neumorphicColors = if (darkTheme) NeumorphicDark else NeumorphicLight

    val neumorphic = NeumorphicColors(
        background = neumorphicColors.Background,
        surface = neumorphicColors.Surface,
        shadowLight = neumorphicColors.ShadowLight,
        shadowDark = neumorphicColors.ShadowDark,
        textPrimary = neumorphicColors.TextPrimary,
        textSecondary = neumorphicColors.TextSecondary,
        accent = neumorphicColors.Accent,
        success = neumorphicColors.Success,
        error = neumorphicColors.Error
    )

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
