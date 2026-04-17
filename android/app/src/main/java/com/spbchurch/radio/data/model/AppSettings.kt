package com.spbchurch.radio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val playbackOrder: PlaybackOrder = PlaybackOrder.SHUFFLE
)

@Serializable
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Serializable
enum class SortOrder {
    DEFAULT,
    A_TO_Z,
    Z_TO_A
}
