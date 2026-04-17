package com.spbchurch.radio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD4A23A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3DB),
    onPrimaryContainer = Color(0xFF2A1600),
    secondary = Color(0xFF6B5E4F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF4E0C9),
    onSecondaryContainer = Color(0xFF251B0F),
    tertiary = Color(0xFF4E6A54),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0EED4),
    onTertiaryContainer = Color(0xFF0B1F12),
    error = Color(0xFFE63835),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201B17),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201B17),
    surfaceVariant = Color(0xFFF0E8E0),
    onSurfaceVariant = Color(0xFF504539),
    outline = Color(0xFF827568),
    outlineVariant = Color(0xFFD4C4B0),
    inverseSurface = Color(0xFF362F2B),
    inverseOnSurface = Color(0xFFFBEEE6),
    inversePrimary = Color(0xFFFFB951),
    surfaceTint = Color(0xFFD4A23A)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB951),
    onPrimary = Color(0xFF462A00),
    primaryContainer = Color(0xFF643F00),
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = Color(0xFFD7C4B0),
    onSecondary = Color(0xFF3A3026),
    secondaryContainer = Color(0xFF52463A),
    onSecondaryContainer = Color(0xFFF4E0C9),
    tertiary = Color(0xFFB5D3BA),
    onTertiary = Color(0xFF213828),
    tertiaryContainer = Color(0xFF384F3E),
    onTertiaryContainer = Color(0xFFD0EED4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFECE0DB),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFECE0DB),
    surfaceVariant = Color(0xFF4F4539),
    onSurfaceVariant = Color(0xFFD3C4B4),
    outline = Color(0xFF9C8E7D),
    outlineVariant = Color(0xFF4F4539),
    inverseSurface = Color(0xFFECE0DB),
    inverseOnSurface = Color(0xFF322B27),
    inversePrimary = Color(0xFFD4A23A),
    surfaceTint = Color(0xFFFFB951)
)

@Composable
fun SPBChurchRadioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
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
