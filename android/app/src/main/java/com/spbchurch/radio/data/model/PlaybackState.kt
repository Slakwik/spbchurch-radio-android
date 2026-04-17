package com.spbchurch.radio.data.model

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isRadioMode: Boolean = true,
    val currentTrack: Track? = null,
    val currentTitle: String = "",
    val progress: Float = 0f,
    val duration: Long = 0L,
    val position: Long = 0L,
    val isBuffering: Boolean = false,
    val playbackOrder: PlaybackOrder = PlaybackOrder.SHUFFLE,
    val error: String? = null
)

enum class PlaybackOrder {
    SHUFFLE,
    REPEAT,
    PLAY_ONCE
}

data class RadioMetadata(
    val title: String = "",
    val isLive: Boolean = false
)
