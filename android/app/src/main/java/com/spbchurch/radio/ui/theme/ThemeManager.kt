package com.spbchurch.radio.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(
    val key: String,
    val displayName: String,
    val icon: ImageVector
) {
    SYSTEM("system", "Системная", Icons.Filled.Brightness6),
    LIGHT("light", "Светлая", Icons.Filled.LightMode),
    DARK("dark", "Тёмная", Icons.Filled.DarkMode);

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}

class ThemeManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _mode = MutableStateFlow(
        ThemeMode.fromKey(prefs.getString(KEY_MODE, ThemeMode.SYSTEM.key))
    )
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    fun setMode(mode: ThemeMode) {
        _mode.value = mode
        prefs.edit().putString(KEY_MODE, mode.key).apply()
    }

    companion object {
        private const val PREFS_NAME = "spbchurch_settings"
        private const val KEY_MODE = "app_theme_mode"
    }
}

val LocalThemeManager = compositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}
