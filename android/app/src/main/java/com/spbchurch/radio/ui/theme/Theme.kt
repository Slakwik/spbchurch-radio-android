package com.spbchurch.radio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AppColors {
    val LightBackground = Color(0xFFF0F0F3)
    val LightSurface = Color(0xFFF2F2F5)
    val LightShadowDark = Color(0xFFA8ABB5).copy(alpha = 0.5f)
    val LightShadowLight = Color.White.copy(alpha = 0.7f)
    val LightTextPrimary = Color(0xFF1F1F24)
    val LightTextSecondary = Color(0xFF737380)

    val DarkBackground = Color(0xFF1C1C24)
    val DarkSurface = Color(0xFF26262E)
    val DarkShadowDark = Color.Black.copy(alpha = 0.6f)
    val DarkShadowLight = Color.White.copy(alpha = 0.06f)
    val DarkTextPrimary = Color(0xFFF2F2F5)
    val DarkTextSecondary = Color(0xFF9999A6)

    val Accent = Color(0xFFD4A23A)
    val AccentLight = Color(0xFFE8BE5A)

    val SuccessLight = Color(0xFF33C75A)
    val SuccessDark = Color(0xFF4DD973)
    val ErrorLight = Color(0xFFE63835)
    val ErrorDark = Color(0xFFF3594C)
}

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Accent,
    onPrimary = Color.White,
    primaryContainer = AppColors.Accent.copy(alpha = 0.15f),
    onPrimaryContainer = AppColors.LightTextPrimary,
    secondary = AppColors.Accent,
    onSecondary = Color.White,
    background = AppColors.LightBackground,
    onBackground = AppColors.LightTextPrimary,
    surface = AppColors.LightSurface,
    onSurface = AppColors.LightTextPrimary,
    surfaceVariant = AppColors.LightSurface,
    onSurfaceVariant = AppColors.LightTextSecondary,
    error = AppColors.ErrorLight,
    onError = Color.White,
    outline = AppColors.LightTextSecondary.copy(alpha = 0.3f)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.AccentLight,
    onPrimary = Color.Black,
    primaryContainer = AppColors.AccentLight.copy(alpha = 0.2f),
    onPrimaryContainer = AppColors.DarkTextPrimary,
    secondary = AppColors.AccentLight,
    onSecondary = Color.Black,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkTextPrimary,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkTextPrimary,
    surfaceVariant = AppColors.DarkSurface,
    onSurfaceVariant = AppColors.DarkTextSecondary,
    error = AppColors.ErrorDark,
    onError = Color.Black,
    outline = AppColors.DarkTextSecondary.copy(alpha = 0.3f)
)

@Composable
fun SPBChurchRadioTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
